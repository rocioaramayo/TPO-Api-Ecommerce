package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "La imagen es demasiado grande. El límite es de 2MB.")
public class ImagenDemasiadoGrandeException extends RuntimeException {
    public ImagenDemasiadoGrandeException() {
        super("La imagen es demasiado grande. El límite es de 2MB.");
    }
}