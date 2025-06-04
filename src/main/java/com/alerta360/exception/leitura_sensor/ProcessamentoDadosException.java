package com.alerta360.exception.leitura_sensor;

public class ProcessamentoDadosException extends RuntimeException {
    public ProcessamentoDadosException(String message) {
        super(message);
    }

    public ProcessamentoDadosException(String message, Throwable cause) {
        super(message, cause);
    }
}
