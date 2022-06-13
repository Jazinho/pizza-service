package com.jpalucki.pizzaservice.config.exception;

import lombok.*;

@Data
public class ValidationException extends Exception {

    private String reason;

    public ValidationException(String reason) {
        this.reason = reason;
    }
}
