package com.uade.tpo.tienda.service.auth;

import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
