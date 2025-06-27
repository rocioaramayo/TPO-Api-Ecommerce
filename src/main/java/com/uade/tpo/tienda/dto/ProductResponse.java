package com.uade.tpo.tienda.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String categoria;
    private Long categoriaId; // ID de la categoría para filtros
    private List<PhotoResponse> fotos;
    private LocalDateTime createdAt;
    private boolean pocoStock;
    private String tipoCuero;
    private String grosor;
    private String acabado;
    private String color;
    private String textura;
    private String instruccionesCuidado;
    private Boolean activo; // Campo para admin - indica si el producto está activo
}