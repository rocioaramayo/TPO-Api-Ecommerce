package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Datos ausentes para actualizar." )
public class FaltanDatosException extends RuntimeException {
}
