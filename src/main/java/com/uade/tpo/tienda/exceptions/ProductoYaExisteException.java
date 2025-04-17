package com.uade.tpo.tienda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ya existe un producto con ese nombre en la misma categoría")
public class ProductoYaExisteException extends RuntimeException {
}
