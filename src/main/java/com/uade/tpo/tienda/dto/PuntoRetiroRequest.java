
package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoRetiroRequest {
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
    private Long metodoEntregaId;
    private Boolean activo;
}