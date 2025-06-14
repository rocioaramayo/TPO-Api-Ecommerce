package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DireccionResponse {
    private Long id;
    private String calle;
    private String numero;
    private String piso;
    private String departamento;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String telefonoContacto;
    private boolean estaActiva;
}
