package com.alerta360.exception.usuario;

public class SenhaIncorretaException extends RuntimeException {
    public SenhaIncorretaException(String message) {
        super(message);
    }

    public SenhaIncorretaException(String message, Throwable cause) {
        super(message, cause);
    }
}
