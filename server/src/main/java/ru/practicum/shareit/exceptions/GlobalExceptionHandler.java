package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(response);
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Map<String, Object>> handleValidateException(ValidateException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error", "Validation error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
//        if (ex.getMessage().contains("PUBLIC.CONSTRAINT_INDEX_4")) {
//
//            Map<String, Object> error = new HashMap<>();
//            error.put("success",false);
//            error.put("error", "Internal error");
//            error.put("message", "Пользователь с таким email уже существует.");
//
//            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
//        }
//        Map<String, Object> error = new HashMap<>();
//        error.put("success",false);
//        error.put("error", "Internal error");
//        error.put("message", ex.getMessage());
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler({DataIntegrityViolationException.class, TransactionSystemException.class})
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(Exception ex) {
        if (ex.getMessage().contains("users_email_key")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Conflict");
            error.put("message", "Пользователь с таким email уже существует.");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .headers(headers)
                    .body(error);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Internal error");
        error.put("message", "Произошла ошибка на сервере");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
