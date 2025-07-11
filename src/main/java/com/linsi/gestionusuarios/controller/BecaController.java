package com.linsi.gestionusuarios.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.linsi.gestionusuarios.service.BecaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.AsignarUsuarioBecaDTO;

@RestController
@RequestMapping("/api/becas")
@RequiredArgsConstructor
@Tag(name = "Gestión de Becas", description = "API para la creación y gestión de las becas")
public class BecaController {

    private final BecaService becaService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public ResponseEntity<List<BecaResponseDTO>> listarBecas() {
        return ResponseEntity.ok(becaService.listarBecas());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<BecaResponseDTO> obtenerBeca(@PathVariable Long id) {
        return ResponseEntity.ok(becaService.obtenerBeca(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<BecaResponseDTO> crearBeca(@Valid @RequestBody BecaRequestDTO becaDto) {
        BecaResponseDTO becaGuardada = becaService.crearBeca(becaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(becaGuardada);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<BecaResponseDTO> actualizarBeca(@PathVariable Long id, @Valid @RequestBody BecaRequestDTO becaDto) {
        return ResponseEntity.ok(becaService.actualizarBeca(id, becaDto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBeca(@PathVariable Long id) {
        becaService.eliminarBeca(id);
        return ResponseEntity.noContent().build();
    }

    //USUARIOS
    
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> asignarUsuarioABeca(
            @PathVariable Long id,
            @PathVariable Long usuarioId) {
        becaService.asignarUsuarioABeca(id, usuarioId);
        return ResponseEntity.noContent().build();
    }    

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}/usuario")
    public ResponseEntity<?> quitarUsuarioDeBeca(@PathVariable Long id) {
        becaService.quitarUsuarioDeBeca(id);
        return ResponseEntity.noContent().build();
    }

}