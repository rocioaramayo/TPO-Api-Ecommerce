package com.uade.tpo.tienda.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompraItemResponse {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
     // ✅ NUEVO: fotos del producto comprado
    private List<PhotoResponse> fotos;
}