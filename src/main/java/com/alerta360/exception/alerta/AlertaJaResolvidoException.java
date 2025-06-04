package com.alerta360.exception.alerta;

public class AlertaJaResolvidoException extends RuntimeException {
    public AlertaJaResolvidoException(String message) {
        super(message);
    }

    public AlertaJaResolvidoException(String message, Throwable cause) {
        super(message, cause);
    }
}