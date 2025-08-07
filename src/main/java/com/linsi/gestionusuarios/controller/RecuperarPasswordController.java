package com.linsi.gestionusuarios.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linsi.gestionusuarios.dto.MensajeResponseDTO;
import com.linsi.gestionusuarios.dto.RecuperarPasswordDTO;
import com.linsi.gestionusuarios.dto.RestablecerPasswordDTO;
import com.linsi.gestionusuarios.service.RecuperarPasswordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RecuperarPasswordController {

    private final RecuperarPasswordService recuperarPasswordService;

    @PostMapping("/recuperar")
    public ResponseEntity<MensajeResponseDTO> solicitarRecuperacion(@Valid @RequestBody RecuperarPasswordDTO dto) {
        recuperarPasswordService.solicitarRecuperacion(dto.getEmail());
        return ResponseEntity.ok(new MensajeResponseDTO("Si el correo electrónico proporcionado está registrado, se enviarán instrucciones para restablecer la contraseña."));
    }

    @PostMapping("/restablecer")
    public ResponseEntity<MensajeResponseDTO> restablecerPassword(@Valid @RequestBody RestablecerPasswordDTO dto) {
        recuperarPasswordService.restablecerPassword(dto.getToken(), dto.getNuevaPassword());
        return ResponseEntity.ok(new MensajeResponseDTO("La contraseña ha sido restablecida exitosamente."));
    }

}
