package com.linsi.gestionusuarios.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Page<ActividadResponseDTO>> listarActividades(Pageable pageable) {
        return ResponseEntity.ok(actividadService.listarActividades(pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponseDTO> obtenerActividad(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.obtenerActividad(id));
    }

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

