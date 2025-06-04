package com.alerta360.exception.sensor;

public class SensorNaoEncontradoException extends RuntimeException {
    public SensorNaoEncontradoException(String message) {
        super(message);
    }

    public SensorNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
