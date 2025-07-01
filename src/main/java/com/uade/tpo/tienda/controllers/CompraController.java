package com.uade.tpo.tienda.controllers;
 
 import java.util.Base64;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
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
import com.uade.tpo.tienda.dto.CompraAdminResponse;
import com.uade.tpo.tienda.dto.CompraItemResponse;
import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.dto.CompraResponse;
import com.uade.tpo.tienda.dto.DireccionResponse;
import com.uade.tpo.tienda.dto.PhotoResponse;
import com.uade.tpo.tienda.dto.PuntoRetiroResponse;
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.Direccion;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.service.Usuario.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;



@RestController
 @RequestMapping("/compras")
 public class CompraController {
 
    @Autowired
    private InterfazCompraService compraService;

    @Autowired
    private CompraService compraService2;

    @Autowired
    private UserService userService;

    
 @PostMapping
    public ResponseEntity<CompraResponse> realizarCompra(Authentication authentication, @RequestBody CompraRequest request) {
        String email = authentication.getName();
        Usuario usuario = userService.getUsuarioByEmail(email);
        if (usuario.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Los administradores no pueden realizar compras.");
        }
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


        // Campos existentes para descuentos
        .subtotal(compra.getSubtotal())
        .codigoDescuento(compra.getCodigoDescuento())
        .porcentajeDescuento(compra.getPorcentajeDescuento())
        .montoDescuento(compra.getMontoDescuento())
        // Campos para métodos de entrega
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
        // Usar la nueva estructura de dirección
        .direccionEntrega(direccionResponse)
        .costoEnvio(compra.getCostoEnvio())
        // Total final
        .total(compra.getTotal())
        .metodoDePago(compra.getMetodoDePago())
        .cuotas(compra.getCuotas())
        .build();
}

@GetMapping("/admin/compras")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<CompraAdminResponse>> listarTodasLasCompras() {
    List<Compra> compras = compraService2.obtenerTodas();
    List<CompraAdminResponse> respuestas = compras.stream()
        .map(compraService2::mapearACompraAdminResponse)
        .toList();
    return ResponseEntity.ok(respuestas);
}

 }