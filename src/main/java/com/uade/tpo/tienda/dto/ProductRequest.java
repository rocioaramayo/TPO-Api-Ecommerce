package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private Long categoryId;  // ID de la categoría a la que pertenece el producto
    private List<PhotoRequest> fotos; // Lista de fotos a asociar al productos
}