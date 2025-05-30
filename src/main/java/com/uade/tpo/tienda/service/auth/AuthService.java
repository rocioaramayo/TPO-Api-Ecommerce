package com.uade.tpo.tienda.service.auth;

import com.uade.tpo.tienda.dto.AdminChangePasswordRequest;
import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.ChangePasswordRequest;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    AuthenticationResponse register(RegisterRequest request);
      void changeOwnPassword(ChangePasswordRequest req);
  void changeAnyPassword(AdminChangePasswordRequest req);

}
