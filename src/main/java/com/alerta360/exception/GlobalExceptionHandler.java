package com.alerta360.exception;

import com.alerta360.exception.alerta.AlertaJaResolvidoException;
import com.alerta360.exception.alerta.AlertaNaoEncontradoException;
import com.alerta360.exception.leitura_sensor.LeituraNaoEncontradaException;
import com.alerta360.exception.sensor.SensorInativoException;
import com.alerta360.exception.sensor.SensorJaExisteException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.exception.usuario.EmailJaExisteException;
import com.alerta360.exception.usuario.SenhaIncorretaException;
import com.alerta360.exception.usuario.UsuarioNaoEncontradoException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Converte as exceções de domínio em respostas HTTP com um corpo de erro padronizado.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({SensorNaoEncontradoException.class, UsuarioNaoEncontradoException.class,
            LeituraNaoEncontradaException.class, AlertaNaoEncontradoException.class})
    public ResponseEntity<ErrorResponse> handleNaoEncontrado(RuntimeException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler({SensorJaExisteException.class, EmailJaExisteException.class,
            AlertaJaResolvidoException.class})
    public ResponseEntity<ErrorResponse> handleConflito(RuntimeException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, "Conflito", ex.getMessage(), request);
    }

    @ExceptionHandler({SensorInativoException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleRequisicaoInvalida(RuntimeException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), request);
    }

    @ExceptionHandler(SenhaIncorretaException.class)
    public ResponseEntity<ErrorResponse> handleSenhaIncorreta(SenhaIncorretaException ex, WebRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Senha incorreta", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Campos obrigatórios não preenchidos ou inválidos",
                request.getDescription(false),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleEntradaMalformada(Exception ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Corpo, parâmetro ou formato da requisição inválido", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRotaInexistente(NoResourceFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, "Rota não encontrada", ex.getMessage(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMetodoNaoSuportado(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método não suportado", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Erro inesperado em {}", request.getDescription(false), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor",
                "Ocorreu um erro inesperado", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, WebRequest request) {
        ErrorResponse body = new ErrorResponse(status.value(), error, message, request.getDescription(false));
        return new ResponseEntity<>(body, status);
    }

    @Getter
    @Setter
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp;
        private Map<String, String> validationErrors;

        public ErrorResponse(int status, String error, String message, String path) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.timestamp = LocalDateTime.now();
        }

        public ErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors) {
            this(status, error, message, path);
            this.validationErrors = validationErrors;
        }
    }
}
