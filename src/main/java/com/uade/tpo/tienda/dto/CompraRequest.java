package com.uade.tpo.tienda.dto;

import java.util.List;

import lombok.Data;


@Data
public class CompraRequest {
    private Long usuarioId;
    private List<CompraItemRequest> items;
}

