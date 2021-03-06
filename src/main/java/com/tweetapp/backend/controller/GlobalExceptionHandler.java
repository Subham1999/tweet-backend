package com.tweetapp.backend.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tweetapp.backend.exceptions.InternalServerException;
import com.tweetapp.backend.exceptions.InvalidRequest;
import com.tweetapp.backend.exceptions.PasswordMismatchException;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
	    final MethodArgumentNotValidException exception) {

	LOGGER.error("Handling MethodArgumentNotValidException msg:{}", exception.getMessage());
	String message = exception.getMessage();
	List<ObjectError> allErrors = exception.getAllErrors();
	Map<String, Object> map = Map.of("err-message", (Object) message, "errors", (Object) allErrors);

	return ResponseEntity.status(465).body(map);
    }

//    @ExceptionHandler(InvalidRequest.class)
//    public ResponseEntity<Map<String, Object>> handleInvalidRequest(final InvalidRequest exception) {
//	String message = exception.getMessage();
//	Map<String,Object> map = Map.of("err-message", message);
//	
//	return ResponseEntity.status(465).body(map);
//    }
//    
    @ExceptionHandler(value = { HttpMessageNotReadableException.class, PasswordMismatchException.class,
	    ExpiredJwtException.class, InternalServerException.class, InvalidRequest.class })
    public ResponseEntity<Map<String, Object>> handleApplicationRuntimeExceptions(final RuntimeException exception) {

	LOGGER.error("Handling RuntimeException::{} msg:{}", exception.getClass().getName(), exception.getMessage());
	String message = exception.getMessage();
	Map<String, Object> map = Map.of("err-message", message);
	int statusCode = 452;

	if (exception instanceof ExpiredJwtException) {
	    statusCode = 460;
	}

	return ResponseEntity.status(statusCode).body(map);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(final NullPointerException exception) {
	String message = exception.getMessage();
	LOGGER.error("Handling NullPointerException::{} msg:{}", exception.getClass().getName(),
		exception.getMessage());
	Map<String, Object> map = Map.of("err-message", message);

	return ResponseEntity.status(466).body(map);
    }
}
