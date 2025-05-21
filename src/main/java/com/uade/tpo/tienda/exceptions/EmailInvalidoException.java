package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException(){
        super("Email no valido. Volver a intentar cumpliendo con los requisitos.");
    }
}
