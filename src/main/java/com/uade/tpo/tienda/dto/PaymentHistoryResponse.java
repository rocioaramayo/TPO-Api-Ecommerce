package com.uade.tpo.tienda.dto;

import com.uade.tpo.tienda.enums.MetodoDePago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {
    private MetodoDePago metodoDePago;
    private Integer cuotasUsadas;
    private LocalDateTime ultimoUso;
    private Integer vecesUtilizado;
}