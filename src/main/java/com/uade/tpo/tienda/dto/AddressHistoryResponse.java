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
public class AddressHistoryResponse {
    private String direccion;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String telefono;
    private LocalDateTime ultimoUso;
    private Integer vecesUtilizada;
}