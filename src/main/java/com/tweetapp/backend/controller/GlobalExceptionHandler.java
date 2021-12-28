package com.tweetapp.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
	String message = exception.getMessage();
	List<ObjectError> allErrors = exception.getAllErrors();
	Map<String,Object> map = Map.of("err-message", (Object) message, "errors", (Object) allErrors);
	
	
	return ResponseEntity.status(465).body(map);
    }
}
