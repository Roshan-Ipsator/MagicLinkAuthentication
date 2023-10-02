package com.ipsator.MagicLinkAuthentication_System.Exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * The global exception handler class
 * 
 * @author Roshan
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * Exception handler method that handles exceptions of type {@link Exception}.
	 * It constructs an {@link ErrorDetails} object with information about the
	 * exception and returns a {@link ResponseEntity} with an HTTP status code of
	 * 400 (Bad Request).
	 *
	 * @param e The exception that was thrown and needs to be handled.
	 * @param w The {@link WebRequest} associated with the request that triggered
	 *          the exception.
	 * @return A {@link ResponseEntity} containing an {@link ErrorDetails} object
	 *         with details about the exception and a status code of 400 (Bad
	 *         Request).
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> anyExceptionHandler(Exception e, WebRequest w) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), e.getMessage(), w.getDescription(false));
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Exception handler method to handle cases where no request handler is found
	 * for a given request path. This method is triggered when a
	 * {@link NoHandlerFoundException} occurs.
	 *
	 * @param e The {@link NoHandlerFoundException} that occurred.
	 * @param w The {@link WebRequest} associated with the request.
	 * @return A {@link ResponseEntity} containing error details and an HTTP status
	 *         code of {@link HttpStatus#BAD_REQUEST}.
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorDetails> noHandlerFoundExceptionHandler(NoHandlerFoundException e, WebRequest w) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), e.getMessage(), w.getDescription(false));
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Exception handler method for handling validation errors raised by Spring MVC
	 * when method argument validation fails. It creates an error response
	 * containing details about the validation error and returns it as a
	 * ResponseEntity with a status of BAD_REQUEST (HTTP 400).
	 *
	 * @param e The MethodArgumentNotValidException that triggered the validation
	 *          error.
	 * @return A ResponseEntity containing error details, including the timestamp of
	 *         the error, a user-friendly error message, and details about the
	 *         validation error, with an HTTP status of BAD_REQUEST.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
		ErrorDetails err = new ErrorDetails(LocalDateTime.now(), "Validation Error..!",
				e.getBindingResult().getFieldError().getDefaultMessage());
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Exception handler method for handling
	 * {@link org.springframework.security.authentication.BadCredentialsException}.
	 * This method is invoked when a {@code BadCredentialsException} is thrown,
	 * typically indicating that the provided credentials are invalid. It returns a
	 * message indicating that the credentials are invalid.
	 *
	 * @return A string message indicating that the credentials are invalid.
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public String exceptionHandler() {
		return "Credentials Invalid !!";
	}
}
