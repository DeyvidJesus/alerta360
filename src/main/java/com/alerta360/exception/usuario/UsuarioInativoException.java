package com.alerta360.exception.usuario;

public class UsuarioInativoException extends RuntimeException {
    public UsuarioInativoException(String message) {
        super(message);
    }

    public UsuarioInativoException(String message, Throwable cause) {
        super(message, cause);
    }
}
