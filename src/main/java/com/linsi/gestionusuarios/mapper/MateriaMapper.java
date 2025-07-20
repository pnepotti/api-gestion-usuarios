package com.linsi.gestionusuarios.mapper;

import org.springframework.stereotype.Component;

import com.linsi.gestionusuarios.dto.MateriaRequestDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.model.Materia;

@Component
public class MateriaMapper {

    public MateriaResponseDTO toDto(Materia materia) {
        if (materia == null) {
            return null;
        }
        MateriaResponseDTO dto = new MateriaResponseDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setCodigo(materia.getCodigo());
        dto.setAnio(materia.getAnio());
        dto.setDescripcion(materia.getDescripcion());
        return dto;
    }

    public Materia toEntity(MateriaRequestDTO dto) {
        Materia materia = new Materia();
        materia.setNombre(dto.getNombre());
        materia.setCodigo(dto.getCodigo());
        materia.setAnio(dto.getAnio());
        materia.setDescripcion(dto.getDescripcion());
        return materia;
    }
}
