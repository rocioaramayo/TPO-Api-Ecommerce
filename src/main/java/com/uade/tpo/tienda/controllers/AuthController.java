package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.AdminChangePasswordRequest;
import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.ChangePasswordRequest;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;
import com.uade.tpo.tienda.dto.UsuarioResponse;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import com.uade.tpo.tienda.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changeOwnPassword(@RequestBody ChangePasswordRequest req) {
        authService.changeOwnPassword(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/change-password")
    public ResponseEntity<Void> changeAnyPassword(@RequestBody AdminChangePasswordRequest req) {
        authService.changeAnyPassword(req);
        return ResponseEntity.ok().build();
    }

    // Listar todos los usuarios (solo ADMIN)

    @GetMapping("/users")
    public ResponseEntity<List<UsuarioResponse>> listAllUsers() {
        List<UsuarioResponse> users = usuarioRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
        return ResponseEntity.ok(users);
    }

    // Detalle de un usuario por Id (solo ADMIN)
    @GetMapping("/users/{id}")
    public ResponseEntity<UsuarioResponse> getUserById(@PathVariable Long id) {
        Usuario user = usuarioRepository.findById(id)
            .orElseThrow(UsuarioNoEncontradoException::new);
        return ResponseEntity.ok(mapToResponse(user));
    }

    // Obtener mi perfil (cualquier usuario autenticado)
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMyProfile(Principal principal) {
        Usuario me = usuarioRepository.findByEmail(principal.getName())
            .orElseThrow(UsuarioNoEncontradoException::new);
        return ResponseEntity.ok(mapToResponse(me));
    }

    private UsuarioResponse mapToResponse(Usuario u) {
        return new UsuarioResponse(
            u.getId(),
            u.getLoginName(),
            u.getEmail(),
            u.getFirstName(),
            u.getLastName(),
            u.getRole(),
            u.isActivo(),
            u.getCreatedAT()
        );
    }

    // Habilitar usuario (ADMIN)
@PutMapping("/users/{id}/habilitar")
public ResponseEntity<Void> habilitarUsuario(@PathVariable Long id) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(UsuarioNoEncontradoException::new);
    usuario.setActivo(true);
    usuarioRepository.save(usuario);
    return ResponseEntity.ok().build();
}

// Deshabilitar usuario (ADMIN)
@PutMapping("/users/{id}/deshabilitar")
public ResponseEntity<Void> deshabilitarUsuario(@PathVariable Long id) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(UsuarioNoEncontradoException::new);
    usuario.setActivo(false);
    usuarioRepository.save(usuario);
    return ResponseEntity.ok().build();
}
}
