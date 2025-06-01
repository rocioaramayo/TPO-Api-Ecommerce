package com.uade.tpo.tienda.controllers;
 
 import java.util.List;
 import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 import com.uade.tpo.tienda.service.compra.*;
import com.uade.tpo.tienda.dto.CompraItemResponse;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.dto.CompraResponse;
import com.uade.tpo.tienda.entity.Compra;
 
 import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
 @RestController

 @RequestMapping("/compras")
 public class CompraController {
 
    @Autowired
    private InterfazCompraService compraService;
    
 @PostMapping
    public ResponseEntity<CompraResponse> realizarCompra(@RequestBody CompraRequest request) {
        Compra compra = compraService.procesarCompra(request);
        CompraResponse response = mapearACompraResponse(compra);
        return ResponseEntity.ok(response);
    }
@GetMapping("/mias")
public ResponseEntity<List<CompraResponse>> obtenerMisCompras(Authentication authentication) {
    String email = authentication.getName();
    List<Compra> compras = compraService.obtenerComprasDeUsuario(email);

    List<CompraResponse> response = compras.stream()
        .map(this::mapearACompraResponse) // ¡Usar el método existente!
        .toList();

    return ResponseEntity.ok(response);
}

@GetMapping
public ResponseEntity<List<CompraResponse>> obtenerTodasLasCompras() {
    List<Compra> compras = compraService.obtenerTodas();

    List<CompraResponse> response = compras.stream()
        .map(this::mapearACompraResponse) // ¡Usar el método existente!
        .toList();

    return ResponseEntity.ok(response);
}

@GetMapping("/{id}")
    public ResponseEntity<CompraResponse> obtenerDetalleCompra(@PathVariable Long id) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        CompraResponse response = compraService.obtenerCompraDelUsuario(id, emailUsuario);
        return ResponseEntity.ok(response);
    }

private CompraResponse mapearACompraResponse(Compra compra) {
    return CompraResponse.builder()
        .id(compra.getId())
        .fecha(compra.getFecha())
        .items(compra.getItems().stream()
            .map(item -> new CompraItemResponse(
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getProducto().getPrecio(),
                item.getCantidad() * item.getProducto().getPrecio()
            ))
            .toList())
        // Campos existentes para descuentos
        .subtotal(compra.getSubtotal())
        .codigoDescuento(compra.getCodigoDescuento())
        .porcentajeDescuento(compra.getPorcentajeDescuento())
        .montoDescuento(compra.getMontoDescuento())
        // Nuevos campos para métodos de entrega
        .metodoEntrega(compra.getMetodoEntrega() != null ? compra.getMetodoEntrega().getNombre() : null)
        .puntoRetiro(compra.getPuntoRetiro() != null ? compra.getPuntoRetiro().getNombre() : null)
        .direccionEntrega(compra.getDireccionEntrega())
        .localidadEntrega(compra.getLocalidadEntrega())
        .provinciaEntrega(compra.getProvinciaEntrega())
        .codigoPostalEntrega(compra.getCodigoPostalEntrega())
        .telefonoContacto(compra.getTelefonoContacto())
        .costoEnvio(compra.getCostoEnvio())
        // Total final
        .total(compra.getTotal())
        .metodoDePago(compra.getMetodoDePago())
        .cuotas(compra.getCuotas())
        .build();
}

 }