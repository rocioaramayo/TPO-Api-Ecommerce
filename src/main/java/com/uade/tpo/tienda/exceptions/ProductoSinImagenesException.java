package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "El producto debe tener al menos una imagen.")
public class ProductoSinImagenesException extends RuntimeException {
    public ProductoSinImagenesException() {
        super("El producto debe tener al menos una imagen.");
    }
}
