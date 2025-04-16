package com.uade.tpo.tienda.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.tienda.dto.CompraRequest;
import com.uade.tpo.tienda.service.compra.CompraService;


@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService = new CompraService();

    @PostMapping
    public ResponseEntity<?> realizarCompra(@RequestBody CompraRequest request) {
        try {
            compraService.procesarCompra(request);
            return ResponseEntity.ok("Compra realizada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

