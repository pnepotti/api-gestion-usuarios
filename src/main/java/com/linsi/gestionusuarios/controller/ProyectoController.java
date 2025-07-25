package com.linsi.gestionusuarios.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.service.ProyectoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@Tag(name = "Gestión de Proyectos", description = "API para la creación y gestión de los proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<ProyectoResponseDTO>> listarProyectos(Pageable pageable) {
        return ResponseEntity.ok(proyectoService.listarProyectos(pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirectorOIntegrante(#id, authentication.principal.id)")
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> obtenerProyecto(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.obtenerProyecto(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping
    public ResponseEntity<ProyectoResponseDTO> crearProyecto(@Valid @RequestBody ProyectoRequestDTO proyectoDto) {
        ProyectoResponseDTO proyectoGuardado = proyectoService.crearProyecto(proyectoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoGuardado);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirector(#id, authentication.principal.id)")
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> actualizarProyecto(@PathVariable Long id, @Valid @RequestBody ProyectoRequestDTO proyectoDto) {
        return ResponseEntity.ok(proyectoService.actualizarProyecto(id, proyectoDto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirector(#id, authentication.principal.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProyecto(@PathVariable Long id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.noContent().build();
    }

    //USUARIOS

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirector(#id, authentication.principal.id)")
    @PutMapping("/{id}/integrantes/{usuarioId}")
    public ResponseEntity<Void> agregarIntegrante(
            @PathVariable Long id,
            @PathVariable Long usuarioId) {
        proyectoService.agregarIntegrante(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirector(#id, authentication.principal.id)")
    @DeleteMapping("/{id}/integrantes/{usuarioId}")
    public ResponseEntity<Void> quitarIntegrante(
            @PathVariable Long id,
            @PathVariable Long usuarioId) {
        proyectoService.quitarIntegrante(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/director/{directorId}")
    public ResponseEntity<Void> asignarDirector(
            @PathVariable Long id,
            @PathVariable Long directorId) {
        proyectoService.asignarDirector(id, directorId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}/director")
    public ResponseEntity<Void> quitarDirector(
            @PathVariable Long id) {
        proyectoService.quitarDirector(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirectorOIntegrante(#id, authentication.principal.id)")
    @GetMapping("/{id}/integrantes")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarIntegrantesDeProyecto(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(proyectoService.listarIntegrantesDeProyecto(id, pageable));
    }
    
    // ACTIVIDADES

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirectorOIntegrante(#id, authentication.principal.id)")
    @GetMapping("/{id}/actividades")
    public ResponseEntity<Page<ActividadResponseDTO>> listarActividadesDeProyecto(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(proyectoService.listarActividadesDeProyecto(id, pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirector(#id, authentication.principal.id)")
    @PostMapping("/{id}/actividades")
    public ResponseEntity<ActividadResponseDTO> crearYAsociarActividadAProyecto(
            @PathVariable Long id,
            @Valid @RequestBody ActividadRequestDTO actividadDto) {
        ActividadResponseDTO actividadGuardada = proyectoService.crearYAsociarActividadAProyecto(id, actividadDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(actividadGuardada);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoSecurity.esDirector(#id, authentication.principal.id)")
    @DeleteMapping("/{id}/actividades/{actividadId}")
    public ResponseEntity<Void> quitarActividadDeProyecto(
            @PathVariable Long id,
            @PathVariable Long actividadId) {
        proyectoService.quitarActividadDeProyecto(id, actividadId);
        return ResponseEntity.noContent().build();
    }
}