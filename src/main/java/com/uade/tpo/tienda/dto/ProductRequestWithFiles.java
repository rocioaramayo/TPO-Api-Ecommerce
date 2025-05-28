package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestWithFiles {
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Long categoryId;
    private List<MultipartFile> imagenes; // Cambio: usar MultipartFile
    private String tipoCuero;
    private String grosor;
    private String acabado;
    private String color;
    private String textura;
    private String instruccionesCuidado;
}