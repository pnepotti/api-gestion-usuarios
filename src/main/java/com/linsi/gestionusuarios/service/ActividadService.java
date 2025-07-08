package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
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

    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> listarActividades() {
        return actividadRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActividadResponseDTO obtenerActividad(Long actividadId) {
        return actividadRepository.findById(actividadId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));
    }

    @Transactional
    public ActividadResponseDTO crearActividad(ActividadRequestDTO actividadDto) {
        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setDescripcion(actividadDto.getDescripcion());
        nuevaActividad.setFecha(actividadDto.getFecha());
        nuevaActividad.setHoras(actividadDto.getHoras());
        Actividad actividadGuardada = actividadRepository.save(nuevaActividad);
        return convertToDto(actividadGuardada);
    }

    @Transactional
    public ActividadResponseDTO actualizarActividad(Long actividadId, ActividadRequestDTO actividadDto) {
        Actividad existente = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId));

        existente.setDescripcion(actividadDto.getDescripcion());
        existente.setFecha(actividadDto.getFecha());
        existente.setHoras(actividadDto.getHoras());

        Actividad actualizada = actividadRepository.save(existente);
        return convertToDto(actualizada);
    }

    @Transactional
    public void eliminarActividad(Long actividadId) {
        if (!actividadRepository.existsById(actividadId)) {
            throw new ResourceNotFoundException("Actividad no encontrada con ID: " + actividadId);
        }
        actividadRepository.deleteById(actividadId);
    }

    private ActividadResponseDTO convertToDto(Actividad actividad) {
        ActividadResponseDTO dto = new ActividadResponseDTO();
        dto.setId(actividad.getId());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setFecha(actividad.getFecha());
        dto.setHoras(actividad.getHoras());
        return dto;
    }
}

