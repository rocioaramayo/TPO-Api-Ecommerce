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
public class CompraAdminResponse {
    private Long id;
    private LocalDateTime fecha;
    private String nombreUsuario;
    private String emailUsuario;
    private List<CompraItemResponse> items;
    private Double subtotal;
    private String codigoDescuento;
    private Double porcentajeDescuento;
    private Double montoDescuento;
    private String metodoEntrega;
    private String puntoRetiro;
    private DireccionResponse direccionEntrega;
    private Double costoEnvio;
    private Double total;
    private MetodoDePago metodoDePago;
    private int cuotas;
}

