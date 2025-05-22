package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.FavoritoRequest;
import com.uade.tpo.tienda.dto.FavoritoResponse;
import com.uade.tpo.tienda.dto.MessageResponse;
import com.uade.tpo.tienda.service.favoritos.FavoritosService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoritosController {

    @Autowired
    private FavoritosService favoritosService;

    @PostMapping
    public ResponseEntity<FavoritoResponse> agregarFavorito(
            Authentication authentication,
            @RequestBody FavoritoRequest request) {
        
        String email = authentication.getName();
        FavoritoResponse response = favoritosService.agregarFavorito(email, request.getProductoId());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<MessageResponse> eliminarFavorito(
            Authentication authentication,
            @PathVariable Long productoId) {
        
        String email = authentication.getName();
        favoritosService.eliminarFavorito(email, productoId);
        
        return ResponseEntity.ok(
            MessageResponse.success("Producto eliminado de favoritos correctamente")
        );
    }

    @GetMapping
    public ResponseEntity<List<FavoritoResponse>> listarFavoritos(Authentication authentication) {
        String email = authentication.getName();
        List<FavoritoResponse> favoritos = favoritosService.obtenerFavoritosUsuario(email);
        
        return ResponseEntity.ok(favoritos);
    }

    @GetMapping("/check/{productoId}")
    public ResponseEntity<Boolean> esFavorito(
            Authentication authentication,
            @PathVariable Long productoId) {
        
        String email = authentication.getName();
        boolean esFavorito = favoritosService.esFavorito(email, productoId);
        
        return ResponseEntity.ok(esFavorito);
    }
}