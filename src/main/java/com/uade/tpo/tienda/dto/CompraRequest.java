package com.uade.tpo.tienda.dto;
 
import java.util.List;

import com.uade.tpo.tienda.enums.MetodoDePago;

import lombok.Data;
 
@Data
public class CompraRequest {
    private List<CompraItemRequest> items;
    private String codigoDescuento;
    private Long metodoEntregaId;
    private Long puntoRetiroId;
    // Reemplazamos los campos individuales de dirección por el ID de una dirección existente
    private Long direccionId;
    private MetodoDePago metodoDePago;
    private int cuotas;
}