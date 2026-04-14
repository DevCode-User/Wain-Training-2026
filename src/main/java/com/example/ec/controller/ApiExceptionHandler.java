package com.example.ec.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" / "));
        Map<String, Object> body = new LinkedHashMap<>(); body.put("result", "failure"); body.put("message", message); body.put("errorCode", "E400");
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("result", "failure"); body.put("message", ex.getMessage()); body.put("errorCode", "E400");
        return ResponseEntity.badRequest().body(body);
    }
}
