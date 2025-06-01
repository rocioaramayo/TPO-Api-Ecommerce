package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompraItemResponse {
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}