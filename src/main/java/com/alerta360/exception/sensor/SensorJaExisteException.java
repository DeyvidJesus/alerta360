package com.alerta360.exception.sensor;

public class SensorJaExisteException extends RuntimeException {
    public SensorJaExisteException(String message) {
        super(message);
    }

    public SensorJaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}

