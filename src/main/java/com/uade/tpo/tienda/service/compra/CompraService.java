package com.uade.tpo.tienda.service.compra;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.dto.CompraAdminResponse;
import com.uade.tpo.tienda.dto.CompraItemRequest;
import com.uade.tpo.tienda.dto.CompraItemResponse;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.dto.CompraResponse;
import com.uade.tpo.tienda.dto.DescuentoAplicadoResponse;
import com.uade.tpo.tienda.dto.DireccionResponse;
import com.uade.tpo.tienda.dto.PhotoResponse;
import com.uade.tpo.tienda.dto.PuntoRetiroResponse;
import com.uade.tpo.tienda.dto.ValidarDescuentoRequest;
import com.uade.tpo.tienda.dto.CotizacionEnvioRequest;
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.CompraItem;
import com.uade.tpo.tienda.entity.Direccion;
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
import com.uade.tpo.tienda.repository.DireccionRepository;
import com.uade.tpo.tienda.repository.MetodoEntregaRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import com.uade.tpo.tienda.repository.PuntoRetiroRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import com.uade.tpo.tienda.service.descuento.DescuentoService;
import com.uade.tpo.tienda.service.entrega.EntregaService;

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
    private DireccionRepository direccionRepository;

    @Autowired
    private DescuentoService descuentoService;

    @Autowired
    private MetodoEntregaRepository metodoEntregaRepository;

    @Autowired
    private PuntoRetiroRepository puntoRetiroRepository;

    @Autowired
    private EntregaService entregaService;

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

        // 5. Manejar método de entrega, punto de retiro y dirección
        MetodoEntrega metodoEntrega = null;
        PuntoRetiro puntoRetiro = null;
        Direccion direccion = null;
        double costoEnvio = 0.0;

        if (request.getMetodoEntregaId() != null) {
            metodoEntrega = metodoEntregaRepository.findById(request.getMetodoEntregaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Método de entrega no encontrado con ID: " + request.getMetodoEntregaId()));

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

            // Si requiere dirección, validar que se proporcione direccionId
            if (metodoEntrega.isRequiereDireccion()) {
                if (request.getDireccionId() == null) {
                    throw new IllegalArgumentException(
                        "El método de entrega seleccionado requiere una dirección");
                }
                direccion = direccionRepository.findById(request.getDireccionId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Dirección no encontrada con ID: " + request.getDireccionId()));
                
                // Verificar que la dirección pertenece al usuario
                if (!direccion.getUsuario().getId().equals(usuario.getId())) {
                    throw new IllegalArgumentException(
                        "La dirección seleccionada no pertenece al usuario actual");
                }
                // Calcular costo de envío usando la lógica de cotización
                CotizacionEnvioRequest cotReq = new CotizacionEnvioRequest();
                cotReq.setDireccion(direccion.getCalle() + " " + direccion.getNumero());
                cotReq.setLocalidad(direccion.getLocalidad());
                cotReq.setProvincia(direccion.getProvincia());
                cotReq.setCodigoPostal(direccion.getCodigoPostal());
                costoEnvio = entregaService.cotizarEnvio(cotReq).getPrecio();
            }
        }

        // 6. Asignar datos de entrega en la compra
        compra.setMetodoEntrega(metodoEntrega);
        compra.setPuntoRetiro(puntoRetiro);
        compra.setDireccionEntrega(direccion);
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

    @Override
    public CompraResponse obtenerCompraDelUsuario(Long idCompra, String emailUsuario) {
        Compra compra = compraRepository.findById(idCompra)
            .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (!compra.getUsuario().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No tienes acceso a esta compra");
        }

        return mapearAResponse(compra);
    }

    private CompraResponse mapearAResponse(Compra compra) {
        List<CompraItemResponse> items = compra.getItems().stream().map(item -> {
    var producto = item.getProducto();

    List<PhotoResponse> fotos = producto.getFotos().stream()
        .map(foto -> {
            byte[] bytes;
            try {
                bytes = foto.getImage().getBytes(1, (int) foto.getImage().length());
            } catch (Exception e) {
                bytes = new byte[0]; // fallback si hay error con el BLOB
            }

            return PhotoResponse.builder()
                .id(foto.getId())
                .file(Base64.getEncoder().encodeToString(bytes))
                .build();
        })
        .toList();

    return CompraItemResponse.builder()
        .nombreProducto(producto.getNombre())
        .cantidad(item.getCantidad())
        .precioUnitario(producto.getPrecio())
        .subtotal(item.getCantidad() * producto.getPrecio())
        .fotos(fotos)
        .build();
}).toList();


        // Mapear dirección si existe
        DireccionResponse direccionResponse = null;
        if (compra.getDireccionEntrega() != null) {
            Direccion dir = compra.getDireccionEntrega();
            direccionResponse = DireccionResponse.builder()
                .id(dir.getId())
                .calle(dir.getCalle())
                .numero(dir.getNumero())
                .piso(dir.getPiso())
                .departamento(dir.getDepartamento())
                .localidad(dir.getLocalidad())
                .provincia(dir.getProvincia())
                .codigoPostal(dir.getCodigoPostal())
                .telefonoContacto(dir.getTelefonoContacto())
                .build();
        }

        return CompraResponse.builder()
            .id(compra.getId())
            .fecha(compra.getFecha())
            .items(items)
            .subtotal(compra.getSubtotal())
            .codigoDescuento(compra.getCodigoDescuento())
            .porcentajeDescuento(compra.getPorcentajeDescuento())
            .montoDescuento(compra.getMontoDescuento())
            .total(compra.getTotal())
            .metodoEntrega(compra.getMetodoEntrega() != null ? compra.getMetodoEntrega().getNombre() : null)
            .puntoRetiro(compra.getPuntoRetiro() != null ? PuntoRetiroResponse.builder()
    .id(compra.getPuntoRetiro().getId())
    .nombre(compra.getPuntoRetiro().getNombre())
    .descripcion(compra.getPuntoRetiro().getDescripcion())
    .direccion(compra.getPuntoRetiro().getDireccion())
    .localidad(compra.getPuntoRetiro().getLocalidad())
    .provincia(compra.getPuntoRetiro().getProvincia())
    .codigoPostal(compra.getPuntoRetiro().getCodigoPostal())
    .horarioAtencion(compra.getPuntoRetiro().getHorarioAtencion())
    .telefono(compra.getPuntoRetiro().getTelefono())
    .email(compra.getPuntoRetiro().getEmail())
    .coordenadas(compra.getPuntoRetiro().getCoordenadas())
    .metodoEntrega(compra.getPuntoRetiro().getMetodoEntrega().getNombre())
    .metodoEntregaId(compra.getPuntoRetiro().getMetodoEntrega().getId())
    .activo(compra.getPuntoRetiro().isActivo())
    .build() : null)
            .direccionEntrega(direccionResponse)
            .costoEnvio(compra.getCostoEnvio())
            .metodoDePago(compra.getMetodoDePago())
            .cuotas(compra.getCuotas())
            .build();
    }


      public CompraAdminResponse mapearACompraAdminResponse(Compra compra) {
    DireccionResponse direccion = null;
    if (compra.getDireccionEntrega() != null) {
        Direccion dir = compra.getDireccionEntrega();
        direccion = DireccionResponse.builder()
            .id(dir.getId())
            .calle(dir.getCalle())
            .numero(dir.getNumero())
            .piso(dir.getPiso())
            .departamento(dir.getDepartamento())
            .localidad(dir.getLocalidad())
            .provincia(dir.getProvincia())
            .codigoPostal(dir.getCodigoPostal())
            .telefonoContacto(dir.getTelefonoContacto())
            .build();
    }

    return CompraAdminResponse.builder()
        .id(compra.getId())
        .fecha(compra.getFecha())
        .nombreUsuario(compra.getUsuario().getFirstName() + " " + compra.getUsuario().getLastName())
        .emailUsuario(compra.getUsuario().getEmail())
        .items(compra.getItems().stream()
    .map(item -> {
        var producto = item.getProducto();

        List<PhotoResponse> fotos = producto.getFotos().stream()
            .map(foto -> {
                byte[] bytes;
                try {
                    bytes = foto.getImage().getBytes(1, (int) foto.getImage().length());
                } catch (Exception e) {
                    bytes = new byte[0]; // fallback si hay error con el BLOB
                }

                return PhotoResponse.builder()
                    .id(foto.getId())
                    .file(Base64.getEncoder().encodeToString(bytes))
                    .build();
            })
            .toList();

        return CompraItemResponse.builder()
            .productoId(producto.getId())
            .nombreProducto(producto.getNombre())
            .cantidad(item.getCantidad())
            .precioUnitario(producto.getPrecio())
            .subtotal(item.getCantidad() * producto.getPrecio())
            .fotos(fotos)
            .build();
    })
    .toList())

        .subtotal(compra.getSubtotal())
        .codigoDescuento(compra.getCodigoDescuento())
        .porcentajeDescuento(compra.getPorcentajeDescuento())
        .montoDescuento(compra.getMontoDescuento())
        .metodoEntrega(compra.getMetodoEntrega() != null ? compra.getMetodoEntrega().getNombre() : null)
        .puntoRetiro(compra.getPuntoRetiro() != null ? compra.getPuntoRetiro().getNombre() : null)
        .direccionEntrega(direccion)
        .costoEnvio(compra.getCostoEnvio())
        .total(compra.getTotal())
        .metodoDePago(compra.getMetodoDePago())
        .cuotas(compra.getCuotas())
        .build();
}



}