package com.alerta360.exception.sensor;

public class SensorInativoException extends RuntimeException {
    public SensorInativoException(String message) {
        super(message);
    }

    public SensorInativoException(String message, Throwable cause) {
        super(message, cause);
    }
}
