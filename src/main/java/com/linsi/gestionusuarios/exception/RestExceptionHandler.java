package com.linsi.gestionusuarios.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.linsi.gestionusuarios.dto.MensajeResponseDTO;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<MensajeResponseDTO> handleEmailExists(EmailAlreadyExistsException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DniAlreadyExistsException.class)
    protected ResponseEntity<MensajeResponseDTO> handleDniExists(DniAlreadyExistsException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LegajoAlreadyExistsException.class)
    protected ResponseEntity<MensajeResponseDTO> handleLegajoExists(LegajoAlreadyExistsException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<MensajeResponseDTO> handleGenericConflict(ConflictException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<MensajeResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO("Credenciales inválidas.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<MensajeResponseDTO> handleInvalidToken(InvalidTokenException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<MensajeResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        MensajeResponseDTO errorResponse = new MensajeResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        Map<String, Object> body = new HashMap<>();
        body.put("mensaje", "Error de validación");
        body.put("errores", fieldErrors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
