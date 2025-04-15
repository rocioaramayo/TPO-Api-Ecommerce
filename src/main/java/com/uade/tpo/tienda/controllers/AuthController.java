package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;
import com.uade.tpo.tienda.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
