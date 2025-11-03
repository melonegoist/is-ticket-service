package edu.itmo.isticketservice.exceptions;

import edu.itmo.isticketservice.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "validation error",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        LOGGER.warn("Validation error: {}", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 400
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(constraintViolation -> {
            String fieldName = constraintViolation.getPropertyPath().toString();
            String errorMessage = constraintViolation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "constraint violation",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        LOGGER.warn("Constraint violation error: {}", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentExceptions(IllegalArgumentException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Illegal argument error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 401 unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(AuthenticationException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Authentication error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 401
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "JWT token expired",
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("JWT token expired: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 403 forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied: " + e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Access denied error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 404
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UserNotFoundException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Username not found error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Entity not found error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 409
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("User already exists error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 409
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Resource already exists error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalExceptions(Exception e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.error("Global error: {}", e.getMessage(), e);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TicketOperationException.class)
    public ResponseEntity<ErrorResponse> handleTicketOperationException(TicketOperationException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Ticket operation error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDiscountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDiscountException(InvalidDiscountException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        LOGGER.warn("Invalid discount error: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* todo

    user not found
    incorrect password
    forbidden -> not authenticated
    try to access not admin -> access denied
    resource not found
    validation error -- bad request
    500 internal server error
    expired jwt token
    global


     */

}
