package com.alerta360.exception.leitura_sensor;

public class LeituraNaoEncontradaException extends RuntimeException {
    public LeituraNaoEncontradaException(String message) {
        super(message);
    }

    public LeituraNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
