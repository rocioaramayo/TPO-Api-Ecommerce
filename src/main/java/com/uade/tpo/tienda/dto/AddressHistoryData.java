package com.uade.tpo.tienda.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddressHistoryData {
    private String direccion;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String telefono;
    private Integer vecesUtilizada = 0;
    private LocalDateTime ultimoUso;
    
    public void incrementarVeces() { 
        this.vecesUtilizada++; 
    }
    
    public void actualizarUltimoUso(LocalDateTime fecha) {
        if (this.ultimoUso == null || fecha.isAfter(this.ultimoUso)) {
            this.ultimoUso = fecha;
        }
    }

}