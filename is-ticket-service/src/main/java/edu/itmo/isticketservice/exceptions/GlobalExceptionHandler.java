package edu.itmo.isticketservice.exceptions;

import edu.itmo.isticketservice.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
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
