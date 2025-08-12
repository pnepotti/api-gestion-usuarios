package com.linsi.gestionusuarios.controller;

import java.util.List;

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

import com.linsi.gestionusuarios.dto.AreaRequestDTO;
import com.linsi.gestionusuarios.dto.AreaResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioRegistroDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.service.AreaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
@Tag(name = "Gestión de Áreas", description = "API para la creación y gestión de áreas")
public class AreaController {

    private final AreaService areaService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<AreaResponseDTO> crearArea(@Valid @RequestBody AreaRequestDTO areaDto) {
        AreaResponseDTO areaGuardada = areaService.crearArea(areaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(areaGuardada);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AreaResponseDTO>> listarAreas() {
        return ResponseEntity.ok(areaService.listarAreas());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArea(@PathVariable Long id) {
        areaService.eliminarArea(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> obtenerArea(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.obtenerArea(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> actualizarArea(@PathVariable Long id, @Valid @RequestBody AreaRequestDTO areaDto) {
        return ResponseEntity.ok(areaService.actualizarArea(id, areaDto));
    }

    // PROYECTOS

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping("/{id}/proyectos")
    public ResponseEntity<ProyectoResponseDTO> crearYAsociarProyectoAlArea(@PathVariable Long id, @Valid @RequestBody ProyectoRequestDTO proyectoDto) {
        ProyectoResponseDTO proyectoCreado = areaService.crearYAsociarProyectoAlArea(id, proyectoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoCreado);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}/proyectos")
    public ResponseEntity<Page<ProyectoResponseDTO>> listarProyectosPorArea(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(areaService.listarProyectosPorArea(id, pageable));
    }

    // USUARIOS

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuariosPorArea(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(areaService.listarUsuariosPorArea(id, pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{id}/usuarios")
    public ResponseEntity<UsuarioResponseDTO> crearYAsociarUsuarioAlArea(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRegistroDTO usuarioDto) {
        UsuarioResponseDTO usuarioCreado = areaService.crearYAsociarUsuarioAlArea(id, usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }
}
