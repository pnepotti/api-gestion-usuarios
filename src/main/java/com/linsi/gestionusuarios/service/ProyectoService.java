package com.linsi.gestionusuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.model.Actividad;
import com.linsi.gestionusuarios.model.Proyecto;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.ActividadRepository;
import com.linsi.gestionusuarios.repository.ProyectoRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;

     // --- Métodos de Proyectos ---

    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> listarProyectos() {
        return proyectoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProyectoResponseDTO obtenerProyecto(Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId));
    }

    @Transactional
    public ProyectoResponseDTO crearProyecto(ProyectoRequestDTO proyectoDto) {
        Proyecto nuevoProyecto = new Proyecto();
        nuevoProyecto.setTitulo(proyectoDto.getTitulo());
        nuevoProyecto.setDescripcion(proyectoDto.getDescripcion());
        nuevoProyecto.setFechaInicio(proyectoDto.getFechaInicio());
        nuevoProyecto.setFechaFin(proyectoDto.getFechaFin());
        nuevoProyecto.setEstado(proyectoDto.getEstado());
        Proyecto proyectoGuardado = proyectoRepository.save(nuevoProyecto);
        return convertToDto(proyectoGuardado);
    }

    @Transactional
    public ProyectoResponseDTO actualizarProyecto(Long proyectoId, ProyectoRequestDTO proyectoDto) {
        Proyecto proyectoExistente = findProyectoById(proyectoId);
        proyectoExistente.setTitulo(proyectoDto.getTitulo());
        proyectoExistente.setDescripcion(proyectoDto.getDescripcion());
        proyectoExistente.setFechaInicio(proyectoDto.getFechaInicio());
        proyectoExistente.setFechaFin(proyectoDto.getFechaFin());
        proyectoExistente.setEstado(proyectoDto.getEstado());
        Proyecto proyectoActualizado = proyectoRepository.save(proyectoExistente);
        return convertToDto(proyectoActualizado);
    }

    @Transactional
    public void eliminarProyecto(Long proyectoId) {
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId);
        }
        proyectoRepository.deleteById(proyectoId);
    }

    // --- Métodos de Integrantes y Director ---

    @Transactional
    public void agregarIntegrante(Long proyectoId, Long usuarioId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (proyecto.getIntegrantes().contains(usuario) || (proyecto.getDirector() != null && proyecto.getDirector().getId().equals(usuarioId))) {
            throw new ConflictException("El usuario ya es parte del proyecto.");
        }

        proyecto.getIntegrantes().add(usuario);
        proyectoRepository.save(proyecto);

    }

    @Transactional
    public void quitarIntegrante(Long proyectoId, Long usuarioId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        boolean removed = proyecto.getIntegrantes().removeIf(integrante -> integrante.getId().equals(usuarioId));
        if (!removed) {
            throw new ResourceNotFoundException("El usuario con ID " + usuarioId + " no es integrante de este proyecto.");
        }
        proyectoRepository.save(proyecto);
    }

    @Transactional
    public void asignarDirector(Long proyectoId, Long directorId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Usuario director = findUsuarioById(directorId);
        proyecto.setDirector(director);
        proyectoRepository.save(proyecto);
    }

    @Transactional
    public void quitarDirector(Long proyectoId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        if (proyecto.getDirector() == null) {
            throw new ResourceNotFoundException("El proyecto con ID " + proyectoId + " no tiene un director asignado.");
        }
        proyecto.setDirector(null);
        proyectoRepository.save(proyecto);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarIntegrantesDeProyecto(Long proyectoId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        return proyecto.getIntegrantes().stream()
                .map(this::convertUsuarioToDto)
                .collect(Collectors.toList());
    }

    // --- Métodos de Actividades ---

    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> listarActividadesDeProyecto(Long proyectoId) {
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId);
        }
        return actividadRepository.findByProyectoId(proyectoId).stream()
                .map(this::convertActividadToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActividadResponseDTO crearYAsociarActividadAProyecto(Long proyectoId, ActividadRequestDTO actividadDto) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setDescripcion(actividadDto.getDescripcion());
        nuevaActividad.setFecha(actividadDto.getFecha());
        nuevaActividad.setHoras(actividadDto.getHoras());
        nuevaActividad.setProyecto(proyecto);
        Actividad actividadGuardada = actividadRepository.save(nuevaActividad);
        return convertActividadToDto(actividadGuardada);
    }
    
    @Transactional
    public void asociarActividadAProyecto(Long proyectoId, Long actividadId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));

        if (actividad.getProyecto() != null) {
            throw new ConflictException("La actividad con ID " + actividadId + " ya está asociada al proyecto con ID " + actividad.getProyecto().getId());
        }

        actividad.setProyecto(proyecto);
        actividadRepository.save(actividad);
    }

    @Transactional
    public void quitarActividadDeProyecto(Long proyectoId, Long actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));

        if (actividad.getProyecto() == null || !actividad.getProyecto().getId().equals(proyectoId)) {
            throw new ConflictException("La actividad no pertenece al proyecto especificado.");
        }
        actividad.setProyecto(null);
        actividadRepository.save(actividad);
    }

    // --- Métodos privados de ayuda y conversión ---

    private Proyecto findProyectoById(Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId));
    }

    private Usuario findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }

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
}
