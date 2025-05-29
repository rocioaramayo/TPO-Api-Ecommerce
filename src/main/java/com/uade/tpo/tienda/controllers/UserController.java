package com.uade.tpo.tienda.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.dto.UsuarioUpdateRequest;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.service.Usuario.UserService;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Jwt;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService ;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listAll() {
        return ResponseEntity.ok(userService.listAllUsers());
    }


    @GetMapping("/{id}")

    public ResponseEntity<UsuarioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(userService.getMyProfile(usuario.getEmail()));
    }



    @PutMapping("/{id}/habilitar")
    public ResponseEntity<Map<String, String>> habilitar(@PathVariable Long id) {
        String mensaje = userService.habilitarUsuario(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        return ResponseEntity.ok(response);
    }

@PutMapping("/me")
public ResponseEntity<UsuarioResponse> updateProfile(
    @AuthenticationPrincipal Usuario usuario,
    @RequestBody UsuarioUpdateRequest req
) {
    UsuarioResponse updated = userService.updateMyProfile(usuario, req);
    return ResponseEntity.ok(updated);
}


    @PutMapping("/{id}/deshabilitar")
    public ResponseEntity<Map<String, String>> deshabilitar(@PathVariable Long id) {
        String mensaje = userService.deshabilitarUsuario(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        return ResponseEntity.ok(response);
    }

}
