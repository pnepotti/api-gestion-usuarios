package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.dto.JwtResponse;
import com.linsi.gestionusuarios.dto.LoginDTO;
import com.linsi.gestionusuarios.dto.MensajeResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioRegistroDTO;
import com.linsi.gestionusuarios.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación y Registro", description = "API para el registro de nuevos usuarios y la obtención de tokens de autenticación (login).")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MensajeResponseDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MensajeResponseDTO("Usuario registrado exitosamente."));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginDTO dto) {
        JwtResponse jwtResponse = authService.login(dto);
        return ResponseEntity.ok(jwtResponse);
    }

}
