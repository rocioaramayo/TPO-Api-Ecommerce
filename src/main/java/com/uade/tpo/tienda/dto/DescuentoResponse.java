package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoResponse {
    private Long id;
    private String codigo;
    private Double porcentaje;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean activo;
    private boolean vigente;
    private Double montoMinimo;
    private String categoria;
}