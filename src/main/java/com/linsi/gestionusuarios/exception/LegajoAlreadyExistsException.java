package com.linsi.gestionusuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class LegajoAlreadyExistsException extends RuntimeException {
    public LegajoAlreadyExistsException(String message) {
        super(message);
    }
}
