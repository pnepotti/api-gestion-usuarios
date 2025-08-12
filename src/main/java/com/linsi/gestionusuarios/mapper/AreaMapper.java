package com.linsi.gestionusuarios.mapper;

import org.springframework.stereotype.Component;

import com.linsi.gestionusuarios.dto.AreaRequestDTO;
import com.linsi.gestionusuarios.dto.AreaResponseDTO;
import com.linsi.gestionusuarios.model.Area;

@Component
public class AreaMapper {

    public AreaResponseDTO toDto(Area area) {
        if (area == null) {
            return null;
        }
        AreaResponseDTO dto = new AreaResponseDTO();
        dto.setId(area.getId());
        dto.setNombre(area.getNombre());
        dto.setDescripcion(area.getDescripcion());
        return dto;
    }

    public Area toEntity(AreaRequestDTO dto) {
        Area area = new Area();
        area.setNombre(dto.getNombre());
        area.setDescripcion(dto.getDescripcion());
        return area;
    }

}
