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
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.ProductoInactivoException;
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.StockInsuficienteException;
import com.uade.tpo.tienda.exceptions.UsuarioInactivoException;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.CompraRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
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

 
    @Transactional
    public Compra procesarCompra(CompraRequest request) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(UsuarioNoEncontradoException::new);

    if (!usuario.isActivo()) {
        throw new UsuarioInactivoException();
    }
    
    Compra compra = new Compra();
    compra.setFecha(LocalDateTime.now());
    compra.setUsuario(usuario);

    // Guardar compra vacía para obtener ID
    compra = compraRepository.save(compra);

    List<CompraItem> items = new ArrayList<>();
    double subtotal = 0;

    for (CompraItemRequest itemReq : request.getItems()) {
        Producto producto = productoRepository.findById(itemReq.getProductoId())
            .orElseThrow(ProductoNoEncontradoException::new);
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

    // Aplicar descuento si se proporciona código
    double total = subtotal;
    if (request.getCodigoDescuento() != null && !request.getCodigoDescuento().isEmpty()) {
        ValidarDescuentoRequest descuentoRequest = new ValidarDescuentoRequest();
        descuentoRequest.setCodigoDescuento(request.getCodigoDescuento());
        descuentoRequest.setItems(request.getItems());
        
        DescuentoAplicadoResponse descuentoAplicado = descuentoService.validarDescuento(descuentoRequest);
        
        if (descuentoAplicado.isDescuentoAplicado()) {
            total = descuentoAplicado.getTotalConDescuento();
            // Opcional: guardar información del descuento aplicado
            compra.setCodigoDescuento(descuentoAplicado.getCodigo());
            compra.setPorcentajeDescuento(descuentoAplicado.getPorcentajeDescuento());
            compra.setMontoDescuento(descuentoAplicado.getMontoDescuento());
        }
    }

    compra.setItems(items);
    compra.setSubtotal(subtotal);
    compra.setTotal(total);

    // Guardar compra final con ítems y total
    compraRepository.save(compra);

    return compra;
}
    public List<Compra> obtenerComprasDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(UsuarioNoEncontradoException::new);
    
        return compraRepository.findByUsuario(usuario);
    }
    public List<Compra> obtenerTodas() {
        return compraRepository.findAll();
    }
}