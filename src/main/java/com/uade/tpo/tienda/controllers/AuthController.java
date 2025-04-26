package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.AdminChangePasswordRequest;
import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.ChangePasswordRequest;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;
import com.uade.tpo.tienda.dto.RegisterResponse;
import com.uade.tpo.tienda.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

@PostMapping("/register")
public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
    AuthenticationResponse authResponse = authService.register(request);

    RegisterResponse response = RegisterResponse.builder()
        .token(authResponse.getToken())
        .mensaje("¡El usuario se creó con éxito!")
        .build();

    return ResponseEntity.ok(response);
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

}
