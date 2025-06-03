package com.uade.tpo.tienda.exceptions;

public class ReviewYaExisteException extends RuntimeException {
    public ReviewYaExisteException() {
        super("Ya has dejado una reseña para este producto");
    }
    
    public ReviewYaExisteException(String message) {
        super(message);
    }
}