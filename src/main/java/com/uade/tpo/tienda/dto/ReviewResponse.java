package com.uade.tpo.tienda.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private String usuario;
    private int estrellas;
    private String comentario;
    private String fecha;
}
