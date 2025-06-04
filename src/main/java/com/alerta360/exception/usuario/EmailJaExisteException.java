package com.alerta360.exception.usuario;

public class EmailJaExisteException extends RuntimeException {
    public EmailJaExisteException(String message) {
        super(message);
    }

    public EmailJaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}
