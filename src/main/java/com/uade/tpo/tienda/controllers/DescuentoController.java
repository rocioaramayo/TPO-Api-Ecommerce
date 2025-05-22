package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.*;
import com.uade.tpo.tienda.service.descuento.DescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/descuentos")
@RequiredArgsConstructor
public class DescuentoController {

    @Autowired
    private DescuentoService descuentoService;

    @PostMapping
    public ResponseEntity<DescuentoResponse> crearDescuento(@RequestBody DescuentoRequest request) {
        DescuentoResponse descuento = descuentoService.crearDescuento(request);
        return ResponseEntity.created(URI.create("/descuentos/" + descuento.getId())).body(descuento);
    }
    
    @GetMapping
    public ResponseEntity<List<DescuentoResponse>> obtenerTodos() {
        return ResponseEntity.ok(descuentoService.obtenerTodosDescuentos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DescuentoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.obtenerDescuentoPorId(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DescuentoResponse> actualizarDescuento(
            @PathVariable Long id, 
            @RequestBody DescuentoRequest request) {
        return ResponseEntity.ok(descuentoService.actualizarDescuento(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminarDescuento(@PathVariable Long id) {
        MessageResponse response = descuentoService.eliminarDescuento(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/activar")
    public ResponseEntity<DescuentoResponse> activarDescuento(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.cambiarEstadoDescuento(id, true));
    }
    
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<DescuentoResponse> desactivarDescuento(@PathVariable Long id) {
        return ResponseEntity.ok(descuentoService.cambiarEstadoDescuento(id, false));
    }
    
    @PostMapping("/validar")
    public ResponseEntity<DescuentoAplicadoResponse> validarDescuento(@RequestBody ValidarDescuentoRequest request) {
        return ResponseEntity.ok(descuentoService.validarDescuento(request));
    }
}
