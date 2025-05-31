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
public class ShippingHistoryResponse {
    private String metodoEntrega;
    private String puntoRetiro;
    private LocalDateTime ultimoUso;
    private Integer vecesUtilizado;
}