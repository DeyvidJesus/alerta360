package com.alerta360.exception.alerta;

public class AlertaNaoEncontradoException extends RuntimeException {
    public AlertaNaoEncontradoException(String message) {
        super(message);
    }

    public AlertaNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}