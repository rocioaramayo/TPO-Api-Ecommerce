package com.uade.tpo.tienda.service.auth;

import com.uade.tpo.tienda.config.JwtService;
import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.enums.Role;
import com.uade.tpo.tienda.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired private
    UsuarioRepository usuarioRepository;
    @Autowired private
    JwtService jwtService;
    @Autowired private
    AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @Override
public AuthenticationResponse register(RegisterRequest request) {
    Role role= request.getRole();
    // si rol no pone nada le pongo por defecto q es comprador
    if(role==null){
        role=Role.COMPRADOR;
    }

    if(role==Role.ADMIN){
        boolean adminYaExiste = usuarioRepository.existsByRole(Role.ADMIN);
        if( adminYaExiste){
            throw new RuntimeException("Ya existe un usuario con rol ADMIN");
        }
    }
    Usuario user = Usuario.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(role)
        .build();

    usuarioRepository.save(user);

    String jwtToken = jwtService.generateToken(user);

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
}
}