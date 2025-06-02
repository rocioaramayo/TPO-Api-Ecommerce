package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.DireccionRequest;
import com.uade.tpo.tienda.dto.DireccionResponse;
import com.uade.tpo.tienda.service.direccion.DireccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;

    @GetMapping("/mias")
    public ResponseEntity<List<DireccionResponse>> obtenerDireccionesDelUsuario() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(direccionService.obtenerDireccionesDelUsuario(email));
    }

    @PostMapping
    public ResponseEntity<Void> guardarDireccion(@RequestBody DireccionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        direccionService.guardarDireccion(email, request);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarDireccion(@PathVariable Long id) {
        direccionService.desactivarDireccion(id);
        return ResponseEntity.ok("Dirección desactivada correctamente.");
    }
}