package com.uade.tpo.tienda.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShippingHistoryData {
    private String metodoEntrega;
    private String puntoRetiro;
    private Integer vecesUtilizado = 0;
    private LocalDateTime ultimoUso;
    
    public void incrementarVeces() { 
        this.vecesUtilizado++; 
    }
    
    public void actualizarUltimoUso(LocalDateTime fecha) {
        if (this.ultimoUso == null || fecha.isAfter(this.ultimoUso)) {
            this.ultimoUso = fecha;
        }
    }
}