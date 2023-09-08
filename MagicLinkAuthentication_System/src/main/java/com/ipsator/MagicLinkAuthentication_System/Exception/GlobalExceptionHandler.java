package com.ipsator.MagicLinkAuthentication_System.Exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> anyExceptionHandler(Exception e, WebRequest w) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), e.getMessage(), w.getDescription(false));
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorDetails> noHandlerFoundExceptionHandler(NoHandlerFoundException e, WebRequest w) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), e.getMessage(), w.getDescription(false));
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), "Validation Error..!",
				e.getBindingResult().getFieldError().getDefaultMessage());
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorDetails> userExceptionHandler(UserException e, WebRequest w) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), e.getMessage(), w.getDescription(false));
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}
}
