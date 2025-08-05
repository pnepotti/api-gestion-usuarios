package com.linsi.gestionusuarios.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.service.BecaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/becas")
@RequiredArgsConstructor
@Tag(name = "Gestión de Becas", description = "API para la creación y gestión de las becas")
public class BecaController {

    private final BecaService becaService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public ResponseEntity<Page<BecaResponseDTO>> listarBecas(Pageable pageable) {
        return ResponseEntity.ok(becaService.listarBecas(pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<BecaResponseDTO> obtenerBeca(@PathVariable Long id) {
        return ResponseEntity.ok(becaService.obtenerBeca(id));
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

}