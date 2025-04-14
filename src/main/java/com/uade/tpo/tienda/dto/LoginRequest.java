package com.uade.tpo.tienda.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
