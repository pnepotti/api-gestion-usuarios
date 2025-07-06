package com.linsi.gestionusuarios.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.linsi.gestionusuarios.model.Actividad;
import com.linsi.gestionusuarios.model.Proyecto;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.dto.AsignarDirectorDTO;
import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.AsignarIntegranteDTO;
import com.linsi.gestionusuarios.dto.MensajeResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.repository.ActividadRepository;
import com.linsi.gestionusuarios.repository.ProyectoRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "Gestión de Proyectos", description = "API para la creación y gestión de los proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoRepository proyectoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    private ProyectoResponseDTO convertToDto(Proyecto proyecto) {
        ProyectoResponseDTO dto = new ProyectoResponseDTO();
        dto.setId(proyecto.getId());
        dto.setTitulo(proyecto.getTitulo());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFin(proyecto.getFechaFin());
        dto.setEstado(proyecto.getEstado());
        if (proyecto.getDirector() != null) {
            dto.setDirector(convertUsuarioToDto(proyecto.getDirector()));
        }
        if (proyecto.getIntegrantes() != null) {
            dto.setIntegrantes(proyecto.getIntegrantes().stream().map(this::convertUsuarioToDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private UsuarioResponseDTO convertUsuarioToDto(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }

    private ActividadResponseDTO convertActividadToDto(Actividad actividad) {
        ActividadResponseDTO dto = new ActividadResponseDTO();
        dto.setId(actividad.getId());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setFecha(actividad.getFecha());
        dto.setHoras(actividad.getHoras());
        return dto;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<ProyectoResponseDTO> listarProyectos() {
        return proyectoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirectorOIntegrante(#proyectoId, authentication.principal.id))")
    @GetMapping("/{proyectoId}")
    public ResponseEntity<ProyectoResponseDTO> obtenerProyecto(@PathVariable Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping
    public ResponseEntity<ProyectoResponseDTO> crearProyecto(@Valid @RequestBody ProyectoRequestDTO proyectoDto) {
        Proyecto nuevoProyecto = new Proyecto();
        nuevoProyecto.setTitulo(proyectoDto.getTitulo());
        nuevoProyecto.setDescripcion(proyectoDto.getDescripcion());
        nuevoProyecto.setFechaInicio(proyectoDto.getFechaInicio());
        nuevoProyecto.setFechaFin(proyectoDto.getFechaFin());
        nuevoProyecto.setEstado(proyectoDto.getEstado());
        Proyecto proyectoGuardado = proyectoRepository.save(nuevoProyecto);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(proyectoGuardado));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirector(#proyectoId, authentication.principal.id))")
    @PutMapping("/{proyectoId}")
    public ResponseEntity<ProyectoResponseDTO> actualizarProyecto(@PathVariable Long proyectoId, @Valid @RequestBody ProyectoRequestDTO proyectoDto) {
        return proyectoRepository.findById(proyectoId).map(proyectoExistente -> {
            proyectoExistente.setTitulo(proyectoDto.getTitulo());
            proyectoExistente.setDescripcion(proyectoDto.getDescripcion());
            proyectoExistente.setFechaInicio(proyectoDto.getFechaInicio());
            proyectoExistente.setFechaFin(proyectoDto.getFechaFin());
            proyectoExistente.setEstado(proyectoDto.getEstado());
            Proyecto proyectoActualizado = proyectoRepository.save(proyectoExistente);
            return ResponseEntity.ok(convertToDto(proyectoActualizado));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirector(#proyectoId, authentication.principal.id))")
    @DeleteMapping("/{proyectoId}")
    public ResponseEntity<Void> eliminarProyecto(@PathVariable Long proyectoId) {
        if (!proyectoRepository.existsById(proyectoId)) {
            return ResponseEntity.notFound().build();
        }
        proyectoRepository.deleteById(proyectoId);
        return ResponseEntity.noContent().build();
    }

    //USUARIOS

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirector(#proyectoId, authentication.principal.id))")
    @PostMapping("/{proyectoId}/integrantes")
    public ResponseEntity<?> agregarIntegrante(
            @PathVariable Long proyectoId,
            @Valid @RequestBody AsignarIntegranteDTO dto) {
         Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Proyecto no encontrado."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Usuario no encontrado."));
        }

        Proyecto proyecto = proyectoOpt.get();
        Usuario usuario = usuarioOpt.get();

        if (proyecto.getIntegrantes().contains(usuario)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MensajeResponseDTO("El usuario ya es integrante del proyecto."));
        }

        proyecto.getIntegrantes().add(usuario);
        proyectoRepository.save(proyecto);
        return ResponseEntity.ok(convertToDto(proyecto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirector(#proyectoId, authentication.principal.id))")
    @DeleteMapping("/{proyectoId}/integrantes/{usuarioId}")
    public ResponseEntity<?> quitarIntegrante(
            @PathVariable Long proyectoId,
            @PathVariable Long usuarioId) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Proyecto no encontrado."));
        }

        Proyecto proyecto = proyectoOpt.get();
        boolean removed = proyecto.getIntegrantes().removeIf(integrante -> integrante.getId().equals(usuarioId));

        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("El usuario no es integrante de este proyecto."));
        }
        proyectoRepository.save(proyecto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{proyectoId}/director")
    public ResponseEntity<?> asignarDirector(
            @PathVariable Long proyectoId,
            @Valid @RequestBody AsignarDirectorDTO dto) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Proyecto no encontrado."));
        }

        Optional<Usuario> directorOpt = usuarioRepository.findById(dto.getDirectorId());
        if (directorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Usuario director no encontrado."));
        }

        Proyecto proyecto = proyectoOpt.get();
        Usuario director = directorOpt.get();
        proyecto.setDirector(director);
        proyectoRepository.save(proyecto);
        return ResponseEntity.ok(convertToDto(proyecto));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirectorOIntegrante(#proyectoId, authentication.principal.id))")
    @GetMapping("/{proyectoId}/integrantes")
    public ResponseEntity<List<UsuarioResponseDTO>> listarIntegrantesDeProyecto(@PathVariable Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .map(proyecto -> {
                    List<UsuarioResponseDTO> integrantesDto = proyecto.getIntegrantes().stream()
                        .map(this::convertUsuarioToDto)
                        .collect(Collectors.toList());
                    return ResponseEntity.ok(integrantesDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ACTIVIDADES

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirectorOIntegrante(#proyectoId, authentication.principal.id)")
    @GetMapping("/{proyectoId}/actividades")
        public ResponseEntity<List<ActividadResponseDTO>> listarActividadesDeProyecto(@PathVariable Long proyectoId) {
        if (!proyectoRepository.existsById(proyectoId)) {
            return ResponseEntity.notFound().build();
        }
        List<Actividad> actividades = actividadRepository.findByProyectoId(proyectoId);
        return ResponseEntity.ok(actividades.stream().map(this::convertActividadToDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirector(#proyectoId, authentication.principal.id)")
    @PostMapping("/{proyectoId}/actividades")
    public ResponseEntity<?> agregarActividadAProyecto(
            @PathVariable Long proyectoId,
            @Valid @RequestBody ActividadRequestDTO actividadDto) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);

        if (proyectoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Proyecto no encontrado con id: " + proyectoId));
        }

        Proyecto proyecto = proyectoOpt.get();
        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setDescripcion(actividadDto.getDescripcion());
        nuevaActividad.setFecha(actividadDto.getFecha());
        nuevaActividad.setHoras(actividadDto.getHoras());
        nuevaActividad.setProyecto(proyecto);

        Actividad actividadGuardada = actividadRepository.save(nuevaActividad);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertActividadToDto(actividadGuardada));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or @proyectoService.esDirector(#proyectoId, authentication.principal.id)")
    @DeleteMapping("/{proyectoId}/actividades/{actividadId}")
    public ResponseEntity<?> quitarActividadDeProyecto(
            @PathVariable Long proyectoId,
            @PathVariable Long actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId).orElse(null);
        if (actividad == null || actividad.getProyecto() == null || !actividad.getProyecto().getId().equals(proyectoId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Actividad no encontrada o no pertenece al proyecto"));
        }
        actividadRepository.deleteById(actividadId);
        return ResponseEntity.noContent().build();
    }
}