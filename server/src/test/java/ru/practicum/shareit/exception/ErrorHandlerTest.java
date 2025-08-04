package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException_shouldReturnBadRequestWithMessage() {
        ValidationException ex = new ValidationException("Validation failed");
        ErrorHandler.ErrorResponse response = errorHandler.handleValidationException(ex);

        assertEquals("Validation failed", response.getError());
    }

    @Test
    void handleNotFoundException_shouldReturnNotFoundWithMessage() {
        NotFoundException ex = new NotFoundException("Not found");
        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(ex);

        assertEquals("Not found", response.getError());
    }

    @Test
    void handleConflictException_shouldReturnConflictWithMessage() {
        ConflictException ex = new ConflictException("Conflict error");
        ErrorHandler.ErrorResponse response = errorHandler.handleConflictException(ex);

        assertEquals("Conflict error", response.getError());
    }

    @Test
    void handleMissingHeader_shouldReturnBadRequestWithMessage() {
        MissingRequestHeaderException ex = new MissingRequestHeaderException("X-Header", null);
        ErrorHandler.ErrorResponse response = errorHandler.handleMissingHeader(ex);

        assertTrue(response.getError().contains("Отсутствует обязательный заголовок"));
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturnBadRequestWithMessage() {
        MethodArgumentNotValidException ex = org.mockito.Mockito.mock(MethodArgumentNotValidException.class);
        var bindingResult = org.mockito.Mockito.mock(org.springframework.validation.BindingResult.class);
        var fieldError = org.mockito.Mockito.mock(org.springframework.validation.FieldError.class);

        org.mockito.Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
        org.mockito.Mockito.when(bindingResult.getFieldError()).thenReturn(fieldError);
        org.mockito.Mockito.when(fieldError.getDefaultMessage()).thenReturn("Некорректное значение");

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentNotValid(ex);

        assertTrue(response.getError().contains("Ошибка валидации"));
    }

    @Test
    void handleHttpMessageNotReadableValid_shouldReturnBadRequestWithMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Cannot read JSON");
        ErrorHandler.ErrorResponse response = errorHandler.handleHttpMessageNotReadableValid(ex);

        assertTrue(response.getError().contains("Ошибка конвертации JSON"));
    }

    @Test
    void handleIllegalArgumentValid_shouldReturnConflictWithMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");
        ErrorHandler.ErrorResponse response = errorHandler.handleIllegalArgumentValid(ex);

        assertEquals("Illegal argument", response.getError());
    }

    @Test
    void handleErrors_shouldReturnInternalServerErrorWithMessage() {
        Throwable ex = new RuntimeException("Unknown error");
        ErrorHandler.ErrorResponse response = errorHandler.handleErrors(ex);

        assertTrue(response.getError().contains("Произошла непредвиденная ошибка"));
    }
}
