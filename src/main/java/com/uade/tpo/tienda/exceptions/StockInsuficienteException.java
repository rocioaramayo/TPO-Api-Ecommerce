package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException() {
        super("No hay stock suficiente para completar la compra.");
    }
}
