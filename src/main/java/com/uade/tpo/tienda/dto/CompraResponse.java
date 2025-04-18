package com.uade.tpo.tienda.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompraResponse {
    private Long id;
    private LocalDateTime fecha;
    private List<CompraItemResponse> items;
    private Double total; 
}