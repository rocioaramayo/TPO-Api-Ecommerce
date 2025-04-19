package com.uade.tpo.tienda.exceptions;

public class UsuarioInactivoException extends RuntimeException {
    public UsuarioInactivoException() {
        super("El usuario ha estado inactivo por más de 2 años");
    }
}