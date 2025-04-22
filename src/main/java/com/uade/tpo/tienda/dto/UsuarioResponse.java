package com.uade.tpo.tienda.dto;

import com.uade.tpo.tienda.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean activo;
    private LocalDateTime createdAt;
}