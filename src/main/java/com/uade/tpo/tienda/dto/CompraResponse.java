package com.uade.tpo.tienda.dto;

import java.time.LocalDateTime;
import java.util.List;

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
    private String puntoRetiro;
    private String direccionEntrega;
    private String localidadEntrega;
    private String provinciaEntrega;
    private String codigoPostalEntrega;
    private String telefonoContacto;
    private Double costoEnvio;
}