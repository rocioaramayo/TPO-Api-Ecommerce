package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)  // Devuelve 403 al cliente
public class UsuarioInactivoException extends RuntimeException {
    public UsuarioInactivoException() {
        super("El usuario está inactivo");
    }
}