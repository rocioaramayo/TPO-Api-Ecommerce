// src/main/java/com/uade/tpo/tienda/service/compra/CompraService.java
package com.uade.tpo.tienda.service.compra;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.dto.CompraItemRequest;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.dto.DescuentoAplicadoResponse;
import com.uade.tpo.tienda.dto.ValidarDescuentoRequest;
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.CompraItem;
import com.uade.tpo.tienda.entity.MetodoEntrega;
import com.uade.tpo.tienda.entity.PuntoRetiro;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.ProductoInactivoException;
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.RecursoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.StockInsuficienteException;
import com.uade.tpo.tienda.exceptions.UsuarioInactivoException;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.CompraRepository;
import com.uade.tpo.tienda.repository.MetodoEntregaRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import com.uade.tpo.tienda.repository.PuntoRetiroRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import com.uade.tpo.tienda.service.descuento.DescuentoService;

import jakarta.transaction.Transactional;

@Service
public class CompraService implements InterfazCompraService {

    @Autowired
    private ProductRepository productoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DescuentoService descuentoService;

    @Autowired
    private MetodoEntregaRepository metodoEntregaRepository;

    @Autowired
    private PuntoRetiroRepository puntoRetiroRepository;

    @Transactional
    public Compra procesarCompra(CompraRequest request) {
        // 1. Obtener usuario autenticado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsuarioNoEncontradoException());
        if (!usuario.isActivo()) {
            throw new UsuarioInactivoException();
        }

        // 2. Crear y persistir compra inicial para generar ID
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setUsuario(usuario);
        compra.setMetodoDePago(request.getMetodoDePago());
        compra.setCuotas(request.getCuotas());
        compra = compraRepository.save(compra);

        // 3. Procesar ítems y calcular subtotal
        List<CompraItem> items = new ArrayList<>();
        double subtotal = 0;
        for (CompraItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                .orElseThrow(() -> new ProductoNoEncontradoException());
            if (!producto.isActivo()) {
                throw new ProductoInactivoException();
            }
            if (producto.getStock() < itemReq.getCantidad()) {
                throw new StockInsuficienteException();
            }
            producto.setStock(producto.getStock() - itemReq.getCantidad());
            productoRepository.save(producto);

            CompraItem item = new CompraItem();
            item.setProducto(producto);
            item.setCantidad(itemReq.getCantidad());
            item.setCompra(compra);

            subtotal += producto.getPrecio() * itemReq.getCantidad();
            items.add(item);
        }

        // 4. Aplicar descuento (si corresponde)
        double total = subtotal;
        if (request.getCodigoDescuento() != null && !request.getCodigoDescuento().isEmpty()) {
            ValidarDescuentoRequest descuentoRequest = new ValidarDescuentoRequest();
            descuentoRequest.setCodigoDescuento(request.getCodigoDescuento());
            descuentoRequest.setItems(request.getItems());

            DescuentoAplicadoResponse aplicado = descuentoService.validarDescuento(descuentoRequest);
            if (aplicado.isDescuentoAplicado()) {
                total = aplicado.getTotalConDescuento();
                compra.setCodigoDescuento(aplicado.getCodigo());
                compra.setPorcentajeDescuento(aplicado.getPorcentajeDescuento());
                compra.setMontoDescuento(aplicado.getMontoDescuento());
            }
        }

        // 5. Manejar método de entrega y punto de retiro
        MetodoEntrega metodoEntrega = null;
        PuntoRetiro puntoRetiro = null;
        double costoEnvio = 0.0;

        if (request.getMetodoEntregaId() != null) {
            metodoEntrega = metodoEntregaRepository.findById(request.getMetodoEntregaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Método de entrega no encontrado con ID: " + request.getMetodoEntregaId()));
            costoEnvio = metodoEntrega.getCostoBase();

            // Si requiere punto de retiro, validar que venga en la request
            if (metodoEntrega.isRequierePuntoRetiro()) {
                if (request.getPuntoRetiroId() == null) {
                    throw new IllegalArgumentException(
                        "El método de entrega seleccionado requiere un punto de retiro");
                }
                puntoRetiro = puntoRetiroRepository.findById(request.getPuntoRetiroId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Punto de retiro no encontrado con ID: " + request.getPuntoRetiroId()));
                if (!puntoRetiro.getMetodoEntrega().getId().equals(metodoEntrega.getId())) {
                    throw new IllegalArgumentException(
                        "El punto de retiro seleccionado no corresponde al método de entrega");
                }
            }

            // Si requiere dirección, validar los campos
            if (metodoEntrega.isRequiereDireccion()) {
                if (request.getDireccionEntrega() == null || request.getDireccionEntrega().isBlank()
                    || request.getLocalidadEntrega() == null || request.getLocalidadEntrega().isBlank()) {
                    throw new IllegalArgumentException(
                        "El método de entrega seleccionado requiere una dirección completa");
                }
            }
        }

        // 6. Asignar datos de entrega en la compra
        compra.setMetodoEntrega(metodoEntrega);
        compra.setPuntoRetiro(puntoRetiro);
        compra.setDireccionEntrega(request.getDireccionEntrega());
        compra.setLocalidadEntrega(request.getLocalidadEntrega());
        compra.setProvinciaEntrega(request.getProvinciaEntrega());
        compra.setCodigoPostalEntrega(request.getCodigoPostalEntrega());
        compra.setTelefonoContacto(request.getTelefonoContacto());
        compra.setCostoEnvio(costoEnvio);

        // 7. Calcular total final incluyendo envío y descuento
        double montoDescuento = compra.getMontoDescuento() != null ? compra.getMontoDescuento() : 0.0;
        total = subtotal - montoDescuento + costoEnvio;

        // 8. Guardar ítems y totales en la compra y persistir
        compra.setItems(items);
        compra.setSubtotal(subtotal);
        compra.setTotal(total);
        compraRepository.save(compra);

        return compra;
    }

    @Override
    public List<Compra> obtenerComprasDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(UsuarioNoEncontradoException::new);
        return compraRepository.findByUsuario(usuario);
    }

    @Override
    public List<Compra> obtenerTodas() {
        return compraRepository.findAll();
    }
}
