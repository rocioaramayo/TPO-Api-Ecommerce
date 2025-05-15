
package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoRetiroResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String horarioAtencion;
    private String telefono;
    private String email;
    private String coordenadas;
    private String metodoEntrega;
    private Long metodoEntregaId;
    private Boolean activo;
}