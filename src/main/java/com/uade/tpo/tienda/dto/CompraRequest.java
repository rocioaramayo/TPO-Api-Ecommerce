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
    private String direccionEntrega;
    private String localidadEntrega;
    private String provinciaEntrega;
    private String codigoPostalEntrega;
    private String telefonoContacto;
    private MetodoDePago metodoDePago;
    private int cuotas;
    }