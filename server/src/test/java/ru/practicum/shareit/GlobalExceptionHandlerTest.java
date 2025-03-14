package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.GlobalExceptionHandler;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundException_ShouldReturn404() {
        NotFoundException exception = new NotFoundException("Объект не найден");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFoundException(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).containsEntry("error", "NotFound error");
        assertThat(response.getBody()).containsEntry("message", "Объект не найден");
    }

    @Test
    void handleAccessDenied_ShouldReturn403() {
        AccessDeniedException exception = new AccessDeniedException("Доступ запрещен");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccessDenied(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        assertThat(response.getBody()).containsEntry("error", "AccessDenied error");
        assertThat(response.getBody()).containsEntry("message", "Доступ запрещен");
    }

    @Test
    void handleValidateException_ShouldReturn400() {
        ValidateException exception = new ValidateException("Ошибка валидации");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidateException(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "Validation error");
        assertThat(response.getBody()).containsEntry("message", "Ошибка валидации");
    }

    @Test
    void handleAllExceptions_ShouldReturn500() {
        Exception exception = new Exception("Неизвестная ошибка");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAllExceptions(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("error", "Internal error");
        assertThat(response.getBody()).containsEntry("message", "Неизвестная ошибка");
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturn400() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("user", "email",
                "Email невалидный")));

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleMethodArgumentNotValidException(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "Validation error");
        assertThat(response.getBody().get("message").toString()).contains("email (Email невалидный)");
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturn500_WhenEmailAlreadyExists() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("PUBLIC.CONSTRAINT_INDEX_4");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolation(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("error", "Internal error");
        assertThat(response.getBody()).containsEntry("message", "Произошла ошибка на сервере");
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturn500_ForOtherErrors() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Ошибка в БД");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolation(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("error", "Internal error");
        assertThat(response.getBody()).containsEntry("message", "Произошла ошибка на сервере");
    }
}
