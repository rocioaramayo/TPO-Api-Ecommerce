package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.*;
import com.uade.tpo.tienda.service.entrega.EntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/entregas")
public class EntregaController {

    @Autowired
    private EntregaService entregaService;
    
    // Endpoints para MetodoEntrega
    
    @PostMapping("/metodos")
    public ResponseEntity<MetodoEntregaResponse> crearMetodoEntrega(@RequestBody MetodoEntregaRequest request) {
        MetodoEntregaResponse response = entregaService.crearMetodoEntrega(request);
        return ResponseEntity.created(URI.create("/entregas/metodos/" + response.getId())).body(response);
    }

    
    @GetMapping("/metodos")
    public ResponseEntity<List<MetodoEntregaResponse>> obtenerMetodosEntrega() {
        return ResponseEntity.ok(entregaService.obtenerMetodosEntrega());
    }
    
    @GetMapping("/metodos/activos")
    public ResponseEntity<List<MetodoEntregaResponse>> obtenerMetodosEntregaActivos() {
        return ResponseEntity.ok(entregaService.obtenerMetodosEntregaActivos());
    }
    
    @GetMapping("/metodos/{id}")
    public ResponseEntity<MetodoEntregaResponse> obtenerMetodoEntregaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.obtenerMetodoEntregaPorId(id));
    }
    
    // @PutMapping("/metodos/{id}")
    // public ResponseEntity<MetodoEntregaResponse> actualizarMetodoEntrega(
    //         @PathVariable Long id, 
    //         @RequestBody MetodoEntregaRequest request) {
    //     return ResponseEntity.ok(entregaService.actualizarMetodoEntrega(id, request));
    // }
    
    @PutMapping("metodos/{id}")
    public ResponseEntity<Void> activarMetodoEntrega(@PathVariable Long id) {
        entregaService.activarMetodoDeEntrega(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/metodos/{id}")
    public ResponseEntity<Void> eliminarMetodoEntrega(@PathVariable Long id) {
        entregaService.eliminarMetodoEntrega(id);
        return ResponseEntity.noContent().build();
    }
    
    // Endpoints para PuntoRetiro
    
    @PostMapping("/puntos")
    public ResponseEntity<PuntoRetiroResponse> crearPuntoRetiro(@RequestBody PuntoRetiroRequest request) {
        PuntoRetiroResponse response = entregaService.crearPuntoRetiro(request);
        return ResponseEntity.created(URI.create("/entregas/puntos/" + response.getId())).body(response);
    }
    
    @GetMapping("/puntos")
    public ResponseEntity<List<PuntoRetiroResponse>> obtenerPuntosRetiro() {
        return ResponseEntity.ok(entregaService.obtenerPuntosRetiro());
    }
    
    @GetMapping("/puntos/activos")
    public ResponseEntity<List<PuntoRetiroResponse>> obtenerPuntosRetiroActivos() {
        return ResponseEntity.ok(entregaService.obtenerPuntosRetiroActivos());
    }
    
    @GetMapping("/puntos/metodo/{metodoId}")
    public ResponseEntity<List<PuntoRetiroResponse>> obtenerPuntosRetiroPorMetodo(@PathVariable Long metodoId) {
        return ResponseEntity.ok(entregaService.obtenerPuntosRetiroPorMetodo(metodoId));
    }
    
    @GetMapping("/puntos/{id}")
    public ResponseEntity<PuntoRetiroResponse> obtenerPuntoRetiroPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.obtenerPuntoRetiroPorId(id));
    }
    
    // @PutMapping("/puntos/{id}")
    // public ResponseEntity<PuntoRetiroResponse> actualizarPuntoRetiro(
    //         @PathVariable Long id, 
    //         @RequestBody PuntoRetiroRequest request) {
    //     return ResponseEntity.ok(entregaService.actualizarPuntoRetiro(id, request));
    // }
    @PutMapping("/puntos/{id}")
    public ResponseEntity<Void> activarPuntoRetiro(@PathVariable Long id) {
        entregaService.activarPuntoRetiro(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/puntos/{id}")
    public ResponseEntity<Void> eliminarPuntoRetiro(@PathVariable Long id) {
        entregaService.eliminarPuntoRetiro(id);
        return ResponseEntity.noContent().build();
    }
    // Endpoint para cotizar envío según dirección/código postal
    @PostMapping("/cotizar")
    public ResponseEntity<CotizacionEnvioResponse> cotizarEnvio(@RequestBody CotizacionEnvioRequest request) {
        CotizacionEnvioResponse response = entregaService.cotizarEnvio(request);
        return ResponseEntity.ok(response);
    }
}