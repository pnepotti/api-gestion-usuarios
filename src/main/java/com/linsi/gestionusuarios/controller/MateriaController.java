package com.linsi.gestionusuarios.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linsi.gestionusuarios.dto.MateriaRequestDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.service.MateriaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/materias")
@RequiredArgsConstructor
@Tag(name = "Gestión de Materias", description = "API para la creación y gestión de las materias")
public class MateriaController {

    private final MateriaService materiaService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<MateriaResponseDTO>> listarMaterias(Pageable pageable) {
        return ResponseEntity.ok(materiaService.listarMaterias(pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @materiaSecurity.esIntegrante(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> obtenerMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaService.obtenerMateria(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crearMateria(@Valid @RequestBody MateriaRequestDTO materiaDto) {
        MateriaResponseDTO materiaGuardada = materiaService.crearMateria(materiaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaGuardada);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> actualizarMateria(@PathVariable Long id, @Valid @RequestBody MateriaRequestDTO materiaDto) {
        return ResponseEntity.ok(materiaService.actualizarMateria(id, materiaDto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable Long id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }

    //USUARIOS

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/integrantes/{usuarioId}")
    public ResponseEntity<Void> asignarUsuarioAMateria(
            @PathVariable Long id,
                      @PathVariable Long usuarioId) {
        materiaService.asignarUsuarioAMateria(id, usuarioId);
        return ResponseEntity.noContent().build();
    }  

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}/integrantes/{usuarioId}")
    public ResponseEntity<Void> quitarUsuarioDeMateria(
            @PathVariable Long id,
            @PathVariable Long usuarioId) {

        materiaService.quitarUsuarioDeMateria(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}/integrantes")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarIntegrantesDeMateria(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(materiaService.listarIntegrantesDeMateria(id, pageable));
    }

}
