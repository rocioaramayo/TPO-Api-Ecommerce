package com.uade.tpo.tienda.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String mensaje;
    private String estado;
    private LocalDateTime timestamp;
    private String detalle;  // Campo opcional para información adicional
    
    // Métodos de utilidad para crear respuestas comunes
    public static MessageResponse success(String mensaje) {
        return MessageResponse.builder()
                .mensaje(mensaje)
                .estado("success")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static MessageResponse error(String mensaje, String detalle) {
        return MessageResponse.builder()
                .mensaje(mensaje)
                .estado("error")
                .timestamp(LocalDateTime.now())
                .detalle(detalle)
                .build();
    }
}