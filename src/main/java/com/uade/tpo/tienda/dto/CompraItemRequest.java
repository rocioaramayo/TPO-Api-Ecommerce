package com.uade.tpo.tienda.dto;
 
import lombok.Data;
 
@Data
public class CompraItemRequest {
    private Long productoId;
    private Integer cantidad;
}
 