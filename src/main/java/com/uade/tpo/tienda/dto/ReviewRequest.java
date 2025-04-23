package com.uade.tpo.tienda.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    private Long productoId;
    private int estrellas;
    private String comentario;
}
