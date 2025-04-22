package com.uade.tpo.tienda.controllers;

import com.uade.tpo.tienda.dto.AdminChangePasswordRequest;
import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.ChangePasswordRequest;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;
import com.uade.tpo.tienda.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

      @PostMapping("/change-password")
  public ResponseEntity<Void> changeOwnPassword(
      @RequestBody ChangePasswordRequest req
  ) {
    authService.changeOwnPassword(req);
    return ResponseEntity.ok().build();
  }

  // 2) Admin cambia contraseña de cualquier usuario
  @PostMapping("/admin/change-password")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> changeAnyPassword(
      @RequestBody AdminChangePasswordRequest req
  ) {
    authService.changeAnyPassword(req);
    return ResponseEntity.ok().build();
  }
}
