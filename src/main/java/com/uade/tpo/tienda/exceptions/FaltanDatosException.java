package com.uade.tpo.tienda.exceptions;

public class FaltanDatosException extends RuntimeException {
    public FaltanDatosException() {
        super("Faltan datos obligatorios para completar la operación");
    }
    
    public FaltanDatosException(String message) {
        super(message);
    }
}