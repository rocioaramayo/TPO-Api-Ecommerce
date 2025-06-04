package com.uade.tpo.tienda.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.uade.tpo.tienda.enums.MetodoDePago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CompraResponse {
    private Long id;
    private LocalDateTime fecha;
    private List<CompraItemResponse> items;
    private Double subtotal;
    private String codigoDescuento;
    private Double porcentajeDescuento;
    private Double montoDescuento;
    private Double total; 
    private String metodoEntrega;

    // Información de dirección como objeto anidado
    private DireccionResponse direccionEntrega;
    private Double costoEnvio;
    private MetodoDePago metodoDePago;
    private int cuotas;
     private PuntoRetiroResponse puntoRetiro;
}