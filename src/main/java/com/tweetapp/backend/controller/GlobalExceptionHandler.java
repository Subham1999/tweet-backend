package com.tweetapp.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tweetapp.backend.exceptions.InternalServerException;
import com.tweetapp.backend.exceptions.InvalidRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
	    final MethodArgumentNotValidException exception) {
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
    @ExceptionHandler(value = { InternalServerException.class, InvalidRequest.class })
    public ResponseEntity<Map<String, Object>> handleApplicationRuntimeExceptions(final RuntimeException exception) {
	String message = exception.getMessage();
	Map<String, Object> map = Map.of("err-message", message);

	return ResponseEntity.status(450).body(map);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(final NullPointerException exception) {
	String message = exception.getMessage();
	Map<String, Object> map = Map.of("err-message", message);

	return ResponseEntity.status(466).body(map);
    }
}
