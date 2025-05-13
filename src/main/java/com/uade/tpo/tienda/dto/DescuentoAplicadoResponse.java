package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoAplicadoResponse {
    private boolean descuentoAplicado;
    private String codigo;
    private Double porcentajeDescuento;
    private Double subtotalSinDescuento;
    private Double montoDescuento;
    private Double totalConDescuento;
    private String mensajeError;
}
