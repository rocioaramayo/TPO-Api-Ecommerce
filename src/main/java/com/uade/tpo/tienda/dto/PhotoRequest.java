package com.uade.tpo.tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRequest {
    private String contenidoBase64; // Contenido de la imagen en Base64
    private String tipoContenido;   // MIME type (image/jpeg, image/png, etc.)
    private String descripcion;
}