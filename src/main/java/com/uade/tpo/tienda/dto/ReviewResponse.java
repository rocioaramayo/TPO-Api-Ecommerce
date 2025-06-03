package com.uade.tpo.tienda.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private String nombreUsuario;  
    private Integer rating;       
    private String comentario;
    private String fecha;
    private String titulo;        
}
