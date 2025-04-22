package com.uade.tpo.tienda.dto;

import lombok.Data;

@Data
public class AdminChangePasswordRequest {
    private String email;
    private String newPassword;
}
