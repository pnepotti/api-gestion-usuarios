package com.linsi.gestionusuarios.mapper;

import com.linsi.gestionusuarios.dto.RolRequestDTO;
import com.linsi.gestionusuarios.dto.RolResponseDTO;
import com.linsi.gestionusuarios.model.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    public RolResponseDTO toDto(Rol rol) {
        if (rol == null) {
            return null;
        }
        RolResponseDTO dto = new RolResponseDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        return dto;
    }

    public Rol toEntity(RolRequestDTO dto) {
        Rol rol = new Rol();
        rol.setNombre(dto.getNombre());
        return rol;
    }
}

