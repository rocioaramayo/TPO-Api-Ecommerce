package com.uade.tpo.tienda.controllers;
 
 import java.util.List;
 import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 import com.uade.tpo.tienda.service.compra.*;
import com.uade.tpo.tienda.dto.CompraItemResponse;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.dto.CompraResponse;
import com.uade.tpo.tienda.entity.Compra;
 
 
 @RestController

 @RequestMapping("/compras")
 public class CompraController {
 
    @Autowired
     private InterfazCompraService compraService;
     @PostMapping
     public ResponseEntity<CompraResponse> realizarCompra(@RequestBody CompraRequest request) {
         try {
             // Procesa y guarda la compra, ahora el método devuelve una Compra
             Compra compra = compraService.procesarCompra(request);
     
             // Mapea la entidad a un DTO limpio para la respuesta
             CompraResponse response = mapearACompraResponse(compra);
     
             return ResponseEntity.ok(response);
         } catch (RuntimeException e) {
             return ResponseEntity.badRequest().body(null);
         }
     }
    @GetMapping("/mias")
public ResponseEntity<List<CompraResponse>> obtenerMisCompras(Authentication authentication) {
    String email = authentication.getName();
    List<Compra> compras = compraService.obtenerComprasDeUsuario(email);

    List<CompraResponse> response = compras.stream().map(compra -> {
        List<CompraItemResponse> items = compra.getItems().stream().map(item ->
            new CompraItemResponse(
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getProducto().getPrecio(),
                item.getCantidad() * item.getProducto().getPrecio()
            )
        ).toList();

        double total = items.stream().mapToDouble(CompraItemResponse::getSubtotal).sum();

        return new CompraResponse(
            compra.getId(),
            compra.getFecha(),
            items,
            total
        );
    }).toList();

    return ResponseEntity.ok(response);
}
@GetMapping
public ResponseEntity<List<CompraResponse>> obtenerTodasLasCompras() {
    List<Compra> compras = compraService.obtenerTodas();

    List<CompraResponse> response = compras.stream().map(compra -> {
        List<CompraItemResponse> items = compra.getItems().stream().map(item ->
            new CompraItemResponse(
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getProducto().getPrecio(),
                item.getCantidad() * item.getProducto().getPrecio()
            )
        ).toList();

        double total = items.stream().mapToDouble(CompraItemResponse::getSubtotal).sum();

        return new CompraResponse(
            compra.getId(),
            compra.getFecha(),
            items,
            total
        );
    }).toList();

    return ResponseEntity.ok(response);
}

private CompraResponse mapearACompraResponse(Compra compra) {
    return new CompraResponse(
        compra.getId(),
        compra.getFecha(),
        compra.getItems().stream()
        .map(item -> new CompraItemResponse(
            item.getProducto().getNombre(),
            item.getCantidad(),
            item.getProducto().getPrecio(), // <- nuevo parámetro: precio unitario
            item.getCantidad() * item.getProducto().getPrecio() // subtotal calculado
        ))
            .toList(),
        compra.getTotal()
    );
}

 }