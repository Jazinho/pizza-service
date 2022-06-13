package com.jpalucki.pizzaservice.config;

import com.jpalucki.pizzaservice.config.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
public class ExceptionHandlerConfig extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> validationException(ValidationException validationException) {
        return new ResponseEntity<>(validationException.getReason(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler
    public ResponseEntity<String> notFoundException(NotFoundException notFoundException) {
        return new ResponseEntity<>(notFoundException.getReason(), HttpStatus.NOT_FOUND);
    }
}
