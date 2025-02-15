package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error", "NotFound error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error", "AccessDenied error");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error", "Internal error");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        StringBuilder messageBuilder = new StringBuilder("Validation failed for fields: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            messageBuilder.append(error.getField())
                    .append(" (")
                    .append(error.getDefaultMessage())
                    .append("); ");
        });
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Validation error");
        response.put("message", messageBuilder.toString().trim());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Map<String, Object>> handleValidateException(ValidateException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error", "Validation error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("PUBLIC.CONSTRAINT_INDEX_4")) {

            Map<String, Object> error = new HashMap<>();
            error.put("success",false);
            error.put("error", "Internal error");
            error.put("message", "Пользователь с таким email уже существует.");

            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        Map<String, Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error", "Internal error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
