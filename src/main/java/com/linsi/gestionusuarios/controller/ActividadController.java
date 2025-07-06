package com.linsi.gestionusuarios.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.model.Actividad;
import com.linsi.gestionusuarios.repository.ActividadRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/actividades")
@Tag(name = "Gestión de Actividades", description = "API para la creación y gestión de las actividades")
public class ActividadController {

    @Autowired
    private ActividadRepository actividadRepository;

    private ActividadResponseDTO convertToDto(Actividad actividad) {
        ActividadResponseDTO dto = new ActividadResponseDTO();
        dto.setId(actividad.getId());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setFecha(actividad.getFecha());
        dto.setHoras(actividad.getHoras());
        return dto;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public List<ActividadResponseDTO> listarActividades() {
        return actividadRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{actividadId}")
    public ResponseEntity<ActividadResponseDTO> obtenerActividad(@PathVariable Long actividadId) {
        return actividadRepository.findById(actividadId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping
    public ResponseEntity<ActividadResponseDTO> crearActividad(@Valid @RequestBody ActividadRequestDTO actividadDto) {
        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setDescripcion(actividadDto.getDescripcion());
        nuevaActividad.setFecha(actividadDto.getFecha());
        nuevaActividad.setHoras(actividadDto.getHoras());
        
        Actividad actividadGuardada = actividadRepository.save(nuevaActividad);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(actividadGuardada));
    }

    //Permisos para Administrador, docente (si la actividad no esta asociada a un proyecto) o, caso contrario, director del proyecto
    @PreAuthorize("@actividadSecurity.puedeModificar(#actividadId, authentication)")
    @PutMapping("/{actividadId}")
    public ResponseEntity<?> actualizarActividad(
            @PathVariable Long actividadId,
            @Valid @RequestBody ActividadRequestDTO actividadDto,
            Authentication authentication) {

        Optional<Actividad> existenteOpt = actividadRepository.findById(actividadId);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Actividad existente = existenteOpt.get();

        existente.setDescripcion(actividadDto.getDescripcion());
        existente.setFecha(actividadDto.getFecha());
        existente.setHoras(actividadDto.getHoras());

        Actividad actualizada = actividadRepository.save(existente);
        return ResponseEntity.ok(convertToDto(actualizada));
    }

     //Permisos para Administrador, docente (si la actividad no esta asociada a un proyecto) o, caso contrario, director del proyecto
    @PreAuthorize("@actividadSecurity.puedeModificar(#actividadId, authentication)")
    @DeleteMapping("/{actividadId}")
     public ResponseEntity<Void> eliminarActividad(
            @PathVariable Long actividadId,
            Authentication authentication) {

        if (!actividadRepository.existsById(actividadId)) {
            return ResponseEntity.notFound().build();
        }

        actividadRepository.deleteById(actividadId);
        return ResponseEntity.noContent().build();
    }

 }

