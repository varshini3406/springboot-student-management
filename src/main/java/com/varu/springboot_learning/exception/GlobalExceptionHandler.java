package com.varu.springboot_learning.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException exception) {

        Map<String, Object> error = Map.of(
                "status", exception.getStatusCode().value(),
                "message", exception.getReason() == null
                        ? "Request failed"
                        : exception.getReason()
        );

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request data");

        Map<String, Object> error = Map.of(
                "status", 400,
                "message", message
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
public ResponseEntity<Map<String, String>> handleRuntimeException(
        RuntimeException ex) {

    if (ex.getMessage() != null
            && ex.getMessage().startsWith("Student not found with id:")) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("message", "An unexpected error occurred"));
}

@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<Map<String, String>> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                    "message",
                    "Invalid value for parameter: " + ex.getName()
            ));
}
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, String>> handleIllegalArgument(
        IllegalArgumentException ex) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                    "message",
                    "Invalid request parameter or value"
            ));
}
}