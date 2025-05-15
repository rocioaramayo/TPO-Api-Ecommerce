
package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoEntregaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double costoBase;
    private Integer tiempoEstimadoDias;
    private Boolean requiereDireccion;
    private Boolean requierePuntoRetiro;
    private Boolean activo;
}