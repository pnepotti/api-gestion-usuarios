package com.linsi.gestionusuarios.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.ActividadMapper;
import com.linsi.gestionusuarios.mapper.ProyectoMapper;
import com.linsi.gestionusuarios.mapper.UsuarioMapper;
import com.linsi.gestionusuarios.model.Actividad;
import com.linsi.gestionusuarios.model.Area;
import com.linsi.gestionusuarios.model.Proyecto;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.ActividadRepository;
import com.linsi.gestionusuarios.repository.AreaRepository;
import com.linsi.gestionusuarios.repository.ProyectoRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;
    private final ProyectoMapper proyectoMapper;
    private final UsuarioMapper usuarioMapper;
    private final ActividadMapper actividadMapper;
    private final AreaRepository areaRepository;

    @Transactional(readOnly = true)
    public Page<ProyectoResponseDTO> listarProyectos(Pageable pageable) {
        Page<Proyecto> proyectos = proyectoRepository.findAll(pageable);
        return proyectos.map(proyectoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProyectoResponseDTO obtenerProyecto(Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .map(proyectoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId));
    }

    @Transactional
    public ProyectoResponseDTO crearProyecto(ProyectoRequestDTO proyectoDto) {
        Proyecto nuevoProyecto = proyectoMapper.toEntity(proyectoDto);
        Proyecto proyectoGuardado = proyectoRepository.save(nuevoProyecto);
        return proyectoMapper.toDto(proyectoGuardado);
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
        return proyectoMapper.toDto(proyectoActualizado);
    }

    @Transactional
    public void eliminarProyecto(Long proyectoId) {
        Proyecto proyecto = findProyectoById(proyectoId);        
        if (proyecto.getDirector() != null) {
            proyecto.getDirector().getProyectosDirigidos().remove(proyecto);
            proyecto.setDirector(null);
        }
        proyecto.borrarArea();
        for (Usuario integrante : new java.util.HashSet<>(proyecto.getIntegrantes())) {
            integrante.getProyectos().remove(proyecto);
        }
        proyecto.getIntegrantes().clear();

        actividadRepository.deleteAll(proyecto.getActividades());
        proyecto.getActividades().clear();

        proyectoRepository.delete(proyecto);
    }

    @Transactional
    public void agregarIntegrante(Long proyectoId, Long usuarioId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (proyecto.getIntegrantes().contains(usuario) || (proyecto.getDirector() != null && proyecto.getDirector().getId().equals(usuarioId))) {
            throw new ConflictException("El usuario ya es parte del proyecto.");
        }

        proyecto.getIntegrantes().add(usuario);
        usuario.getProyectos().add(proyecto);

    }

    @Transactional
    public void quitarIntegrante(Long proyectoId, Long usuarioId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (proyecto.getIntegrantes().contains(usuario)) {
            proyecto.getIntegrantes().remove(usuario);
            usuario.getProyectos().remove(proyecto);
        } else {
            throw new ResourceNotFoundException("El usuario con ID " + usuarioId + " no es integrante de este proyecto.");
        }
    }

    @Transactional
    public void asignarDirector(Long proyectoId, Long directorId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Usuario director = findUsuarioById(directorId);

        if (proyecto.getDirector() != null) {
            proyecto.getDirector().getProyectosDirigidos().remove(proyecto);
        }
        proyecto.setDirector(director);
        director.getProyectosDirigidos().add(proyecto);
    }

    @Transactional
    public void quitarDirector(Long proyectoId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Usuario directorActual = proyecto.getDirector();
        if (directorActual == null) {
            throw new ResourceNotFoundException("El proyecto con ID " + proyectoId + " no tiene un director asignado.");
        }
        proyecto.setDirector(null);
        directorActual.getProyectosDirigidos().remove(proyecto);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarIntegrantesDeProyecto(Long proyectoId, Pageable pageable) {
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId);
        }
        Page<Usuario> integrantes = usuarioRepository.findByProyectos_Id(proyectoId, pageable);
        return integrantes.map(usuarioMapper::toDto);
    }

    // ACTIVIDADES

    @Transactional(readOnly = true)
    public Page<ActividadResponseDTO> listarActividadesDeProyecto(Long proyectoId, Pageable pageable) {
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId);
        }
        return actividadRepository.findByProyectoId(proyectoId, pageable).map(actividadMapper::toDto);
    }

    @Transactional
    public ActividadResponseDTO crearYAsociarActividadAProyecto(Long proyectoId, ActividadRequestDTO actividadDto) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Actividad nuevaActividad = actividadMapper.toEntity(actividadDto);
        nuevaActividad.setProyecto(proyecto);
        proyecto.getActividades().add(nuevaActividad);
        Actividad actividadGuardada = actividadRepository.save(nuevaActividad);
        return actividadMapper.toDto(actividadGuardada);
    }

    @Transactional
    public void quitarActividadDeProyecto(Long proyectoId, Long actividadId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));

        if (actividad.getProyecto() == null || !actividad.getProyecto().getId().equals(proyectoId)) {
            throw new ConflictException("La actividad no pertenece al proyecto especificado.");
        }
        actividad.setProyecto(null);
        proyecto.getActividades().remove(actividad);
    }

    // ÁREAS

    @Transactional
    public void asignarArea(Long proyectoId, Long areaId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        Area nuevaArea = findAreaById(areaId);

        // Desvincular del área anterior si existe
        if (proyecto.getArea() != null) {
            proyecto.getArea().getProyectos().remove(proyecto);
        }

        // Vincular a la nueva área
        proyecto.setArea(nuevaArea);
        nuevaArea.getProyectos().add(proyecto);
    }

    @Transactional
    public void quitarArea(Long proyectoId) {
        Proyecto proyecto = findProyectoById(proyectoId);
        if (proyecto.getArea() == null) {
            throw new ResourceNotFoundException("El proyecto con ID " + proyectoId + " no tiene un área asignada.");
        }
        proyecto.borrarArea();
    }

    // --- Métodos privados de ayuda ---

    private Proyecto findProyectoById(Long proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + proyectoId));
    }

    private Usuario findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }

    private Area findAreaById(Long areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + areaId));
    }
}
