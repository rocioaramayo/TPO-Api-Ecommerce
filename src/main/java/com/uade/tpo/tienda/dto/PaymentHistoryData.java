package com.uade.tpo.tienda.dto;

import com.uade.tpo.tienda.enums.MetodoDePago;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentHistoryData {
    private MetodoDePago metodo;
    private Integer vecesUtilizado = 0;
    private LocalDateTime ultimoUso;
    private Integer cuotasUsadas = 0;
    
    public void incrementarVeces() { 
        this.vecesUtilizado++; 
    }
    
    public void actualizarUltimoUso(LocalDateTime fecha) {
        if (this.ultimoUso == null || fecha.isAfter(this.ultimoUso)) {
            this.ultimoUso = fecha;
        }
    }
    
    public void agregarCuotas(int cuotas) {
        if (this.cuotasUsadas == null || cuotas > this.cuotasUsadas) {
            this.cuotasUsadas = cuotas;
        }
    }
}

