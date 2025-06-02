package com.uade.tpo.tienda.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DireccionRequest {
    private String calle;
    private String numero;
    private String piso;
    private String departamento;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String telefonoContacto;
}
