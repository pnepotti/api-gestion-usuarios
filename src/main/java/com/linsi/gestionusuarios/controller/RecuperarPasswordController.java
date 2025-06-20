package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.dto.RecuperarPasswordDTO;
import com.linsi.gestionusuarios.dto.RestablecerPasswordDTO;
import com.linsi.gestionusuarios.service.RecuperarPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RecuperarPasswordController {

    private final RecuperarPasswordService recuperarPasswordService;

    @PostMapping("/recuperar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody RecuperarPasswordDTO dto) {        
        try {
            recuperarPasswordService.solicitarRecuperacion(dto.getEmail());
            return ResponseEntity.ok("Si el correo está registrado, se enviaron instrucciones.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody RestablecerPasswordDTO dto) {
        try {
            recuperarPasswordService.restablecerPassword(dto.getToken(), dto.getNuevaPassword());
            return ResponseEntity.ok("Contraseña restablecida correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
