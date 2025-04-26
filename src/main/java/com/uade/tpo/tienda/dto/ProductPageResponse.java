package com.uade.tpo.tienda.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponse {
    private List<ProductResponse> productos;
    private long totalProductos;
    private int paginaActual;
    private int tamañoPagina;
}
