package com.uade.tpo.tienda.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Long categoryId;
    private String tipoCuero;
    private String grosor;
    private String acabado;
    private String color;
    private String textura;
    private String instruccionesCuidado;
    private Boolean activo;
}