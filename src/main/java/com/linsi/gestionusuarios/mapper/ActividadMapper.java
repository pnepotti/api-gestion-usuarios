package com.linsi.gestionusuarios.mapper;

import org.springframework.stereotype.Component;

import com.linsi.gestionusuarios.dto.ActividadRequestDTO;
import com.linsi.gestionusuarios.dto.ActividadResponseDTO;
import com.linsi.gestionusuarios.model.Actividad;

@Component
public class ActividadMapper {

    public ActividadResponseDTO toDto(Actividad actividad) {
        if (actividad == null) {
            return null;
        }
        ActividadResponseDTO dto = new ActividadResponseDTO();
        dto.setId(actividad.getId());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setFecha(actividad.getFecha());
        dto.setHoras(actividad.getHoras());
        return dto;
    }

    public Actividad toEntity(ActividadRequestDTO dto) {
        Actividad actividad = new Actividad();
        actividad.setDescripcion(dto.getDescripcion());
        actividad.setFecha(dto.getFecha());
        actividad.setHoras(dto.getHoras());
        return actividad;
    }
}
