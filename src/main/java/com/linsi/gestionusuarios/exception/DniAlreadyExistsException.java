package com.linsi.gestionusuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DniAlreadyExistsException extends RuntimeException {
    public DniAlreadyExistsException(String message) {
        super(message);
    }
}
