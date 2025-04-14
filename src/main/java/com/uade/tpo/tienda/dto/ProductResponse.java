package com.uade.tpo.tienda.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String categoria;    // Por ejemplo, el nombre o descripción de la categoría
    private String vendedor;     // Aquí puedes poner el username o el nombre completo del vendedor
    private List<PhotoResponse> fotos;
    private LocalDateTime createdAt;
}
