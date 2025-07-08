package com.linsi.gestionusuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.linsi.gestionusuarios.dto.AsignarIntegranteMateriaDTO;
import com.linsi.gestionusuarios.dto.MateriaRequestDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;

import com.linsi.gestionusuarios.service.MateriaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
@Tag(name = "Gestión de Materias", description = "API para la creación y gestión de las materias")
public class MateriaController {

    private final MateriaService materiaService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> listarMaterias() {
        return ResponseEntity.ok(materiaService.listarMaterias());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> obtenerMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaService.obtenerMateria(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crearMateria(@Valid @RequestBody MateriaRequestDTO materiaDto) {
        MateriaResponseDTO materiaGuardada = materiaService.crearMateria(materiaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaGuardada);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> actualizarMateria(@PathVariable Long id, @Valid @RequestBody MateriaRequestDTO materiaDto) {
        return ResponseEntity.ok(materiaService.actualizarMateria(id, materiaDto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable Long id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }

    //USUARIOS

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping("/{id}/integrantes")
    public ResponseEntity<MateriaResponseDTO> asignarUsuarioAMateria(
            @PathVariable Long id,
            @Valid @RequestBody AsignarIntegranteMateriaDTO dto) {

        return ResponseEntity.ok(materiaService.asignarUsuarioAMateria(id, dto.getUsuarioId()));
    }    

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @DeleteMapping("/{id}/integrantes/{usuarioId}")
    public ResponseEntity<Void> quitarUsuarioDeMateria(
            @PathVariable Long id,
            @PathVariable Long usuarioId) {

        materiaService.quitarUsuarioDeMateria(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}/integrantes")
    public ResponseEntity<List<UsuarioResponseDTO>> listarIntegrantesDeMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaService.listarIntegrantesDeMateria(id));
    }

}
