package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403
public class ProductoInactivoException extends RuntimeException {
    
    public ProductoInactivoException() {
        super("El producto está inactivo");
    }
}