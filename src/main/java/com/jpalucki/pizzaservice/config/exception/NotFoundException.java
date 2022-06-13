package com.jpalucki.pizzaservice.config.exception;

import lombok.*;

@Data
public class NotFoundException extends Exception {

    private String reason;

    public NotFoundException(String reason) {
        this.reason = reason;
    }
}
