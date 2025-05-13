package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDescuentoRequest {
    private String codigoDescuento;
    private List<CompraItemRequest> items;
}
