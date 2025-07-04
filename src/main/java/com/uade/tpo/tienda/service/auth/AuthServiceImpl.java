package com.uade.tpo.tienda.service.auth;

import com.uade.tpo.tienda.config.JwtService;
import com.uade.tpo.tienda.dto.AuthenticationResponse;
import com.uade.tpo.tienda.dto.LoginRequest;
import com.uade.tpo.tienda.dto.LoginResponse;
import com.uade.tpo.tienda.dto.RegisterRequest;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.enums.Role;
import com.uade.tpo.tienda.exceptions.EmailInvalidoException;
import com.uade.tpo.tienda.exceptions.UsuarioInactivoException;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.exceptions.UsuarioYaExisteException;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import com.uade.tpo.tienda.dto.ChangePasswordRequest;
import com.uade.tpo.tienda.dto.AdminChangePasswordRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        if(!EmailValidator.esEmailValido(request.getEmail())){
            throw new EmailInvalidoException();
        }
        
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(UsuarioNoEncontradoException::new);
    
        if (!user.isActivo()) {
            throw new UsuarioInactivoException();
         }
    
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
    
        String jwt = jwtService.generateToken(user);
    
        return LoginResponse.builder()
            .token(jwt)
            .role(user.getRole().name())
            .usuario(user)
            .build();
    }
    

    
    @Override
public AuthenticationResponse register(RegisterRequest request) {
    if(!EmailValidator.esEmailValido(request.getEmail())){
        throw new EmailInvalidoException();
    }

    if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new UsuarioYaExisteException();
    }

    if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
        throw new UsuarioYaExisteException();
    }
    // validar longitud mínima de contraseña
    if (request.getPassword() == null || request.getPassword().length() < 8) {
        throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
    }
    Role role= request.getRole();
    // si rol no pone nada le pongo por defecto q es comprador
    if(role==null){
        role=Role.COMPRADOR;
    }
    boolean activo = true;

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
        .activo(activo)
        .build();

    usuarioRepository.save(user);

    String jwtToken = jwtService.generateToken(user);

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
}
@Override
public void changeOwnPassword(ChangePasswordRequest req) {
    // tomo el email del usuario logueado
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    Usuario user = usuarioRepository.findByEmail(email)
        .orElseThrow(UsuarioNoEncontradoException::new);
    // valido que la contraseña actual coincida
    if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Contraseña actual inválida");
    }
    if (req.getNewPassword().length() < 8){
        throw new IllegalArgumentException("Contraseña nueva inválida");
    }else{
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        usuarioRepository.save(user);
    }
}

@Override
public void changeAnyPassword(AdminChangePasswordRequest req) {
    // sólo admin 
    Usuario target = usuarioRepository.findByEmail(req.getEmail())
        .orElseThrow(UsuarioNoEncontradoException::new);
    target.setPassword(passwordEncoder.encode(req.getNewPassword()));
    usuarioRepository.save(target);
}


}