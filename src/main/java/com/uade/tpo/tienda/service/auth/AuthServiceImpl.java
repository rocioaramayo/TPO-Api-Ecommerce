package com.uade.tpo.tienda.service.auth;

import com.uade.tpo.tienda.config.JwtService;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired final
    UsuarioRepository usuarioRepository;
    @Autowired final 
    JwtService jwtService;
    @Autowired final 
    AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        UserDetails user = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwt = jwtService.generateToken(user);

        return LoginResponse.builder()
            .token(jwt)
            .build();
    }
}
