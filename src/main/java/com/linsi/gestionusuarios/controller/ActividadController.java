package com.linsi.gestionusuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.service.ActividadService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
@Tag(name = "Gestión de Actividades", description = "API para la creación y gestión de las actividades")
public class ActividadController {

    private final ActividadService actividadService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public ResponseEntity<List<ActividadResponseDTO>> listarActividades() {
        return ResponseEntity.ok(actividadService.listarActividades());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponseDTO> obtenerActividad(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.obtenerActividad(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping
    public ResponseEntity<ActividadResponseDTO> crearActividad(@Valid @RequestBody ActividadRequestDTO actividadDto) {
        ActividadResponseDTO actividadGuardada = actividadService.crearActividad(actividadDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(actividadGuardada);
    }

    //Permisos para Administrador, docente (si la actividad no esta asociada a un proyecto) o, caso contrario, director del proyecto
    @PreAuthorize("@actividadSecurity.puedeModificar(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<ActividadResponseDTO> actualizarActividad(
            @PathVariable Long id,
            @Valid @RequestBody ActividadRequestDTO actividadDto,
            Authentication authentication) {

        ActividadResponseDTO actualizada = actividadService.actualizarActividad(id, actividadDto);
        return ResponseEntity.ok(actualizada);
    }

     //Permisos para Administrador, docente (si la actividad no esta asociada a un proyecto) o, caso contrario, director del proyecto
    @PreAuthorize("@actividadSecurity.puedeModificar(#id, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarActividad(
            @PathVariable Long id,
            Authentication authentication) {

        actividadService.eliminarActividad(id);
        return ResponseEntity.noContent().build();
    }

 }

