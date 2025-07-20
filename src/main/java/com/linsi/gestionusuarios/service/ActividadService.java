package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.ActividadMapper;
import com.linsi.gestionusuarios.model.Actividad;
import com.linsi.gestionusuarios.repository.ActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final ActividadMapper actividadMapper;

    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> listarActividades() {
        return actividadRepository.findAll().stream()
                .map(actividadMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActividadResponseDTO obtenerActividad(Long actividadId) {
        return actividadRepository.findById(actividadId)
                .map(actividadMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));
    }

    @Transactional
    public ActividadResponseDTO actualizarActividad(Long actividadId, ActividadRequestDTO actividadDto) {
        Actividad existente = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));

        existente.setDescripcion(actividadDto.getDescripcion());
        existente.setFecha(actividadDto.getFecha());
        existente.setHoras(actividadDto.getHoras());

        Actividad actualizada = actividadRepository.save(existente);
        return actividadMapper.toDto(actualizada);
    }

    @Transactional
    public void eliminarActividad(Long actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));

        if (actividad.getProyecto() != null) {
            actividad.getProyecto().getActividades().remove(actividad);
        }
        actividadRepository.delete(actividad);

    }
}

