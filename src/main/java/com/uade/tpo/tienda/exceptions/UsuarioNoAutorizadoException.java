package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsuarioNoAutorizadoException extends RuntimeException {
    public UsuarioNoAutorizadoException() {
        super("Solo los usuarios que compraron este producto pueden dejar una reseña.");
    }
}
