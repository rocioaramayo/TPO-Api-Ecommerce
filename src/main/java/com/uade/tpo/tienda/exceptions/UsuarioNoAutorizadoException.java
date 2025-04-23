package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsuarioNoAutorizadoException extends RuntimeException {
    public UsuarioNoAutorizadoException() {
        super("No estás autorizado para dejar una reseña.");
    }
}
