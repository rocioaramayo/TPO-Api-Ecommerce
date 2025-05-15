
package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoEntregaRequest {
    private String nombre;
    private String descripcion;
    private Double costoBase;
    private Integer tiempoEstimadoDias;
    private Boolean requiereDireccion;
    private Boolean requierePuntoRetiro;
    private Boolean activo;
}