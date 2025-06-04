package com.alerta360.exception;

import com.alerta360.exception.leitura_sensor.LeituraNaoEncontradaException;
import com.alerta360.exception.sensor.SensorInativoException;
import com.alerta360.exception.sensor.SensorJaExisteException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.exception.usuario.EmailJaExisteException;
import com.alerta360.exception.usuario.SenhaIncorretaException;
import com.alerta360.exception.usuario.UsuarioNaoEncontradoException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SensorNaoEncontradoException.class)
  public ResponseEntity<ErrorResponse> handleSensorNaoEncontrado(
          SensorNaoEncontradoException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Sensor não encontrado",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(SensorJaExisteException.class)
  public ResponseEntity<ErrorResponse> handleSensorJaExiste(
          SensorJaExisteException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Sensor já existe",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(SensorInativoException.class)
  public ResponseEntity<ErrorResponse> handleSensorInativo(
          SensorInativoException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Sensor inativo",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UsuarioNaoEncontradoException.class)
  public ResponseEntity<ErrorResponse> handleUsuarioNaoEncontrado(
          UsuarioNaoEncontradoException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Usuário não encontrado",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(EmailJaExisteException.class)
  public ResponseEntity<ErrorResponse> handleEmailJaExiste(
          EmailJaExisteException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Email já existe",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(SenhaIncorretaException.class)
  public ResponseEntity<ErrorResponse> handleSenhaIncorreta(
          SenhaIncorretaException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Senha incorreta",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(LeituraNaoEncontradaException.class)
  public ResponseEntity<ErrorResponse> handleLeituraNaoEncontrada(
          LeituraNaoEncontradaException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Leitura não encontrada",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
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

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
          IllegalArgumentException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Argumento inválido",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(
          RuntimeException ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            ex.getMessage(),
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
          Exception ex, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            "Ocorreu um erro inesperado",
            request.getDescription(false)
    );
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Setter
  @Getter
  public static class ErrorResponse {
      // Getters and Setters
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