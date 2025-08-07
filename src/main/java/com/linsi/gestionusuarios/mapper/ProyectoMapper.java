package com.linsi.gestionusuarios.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.model.Proyecto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoMapper {

    private final UsuarioMapper usuarioMapper;

    public ProyectoResponseDTO toDto(Proyecto proyecto) {
        if (proyecto == null) {
            return null;
        }
        ProyectoResponseDTO dto = new ProyectoResponseDTO();
        dto.setId(proyecto.getId());
        dto.setTitulo(proyecto.getTitulo());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFin(proyecto.getFechaFin());
        dto.setEstado(proyecto.getEstado());
        if (proyecto.getDirector() != null) {
            dto.setDirector(usuarioMapper.toDto(proyecto.getDirector()));
        }
        if (proyecto.getIntegrantes() != null) {
            dto.setIntegrantes(proyecto.getIntegrantes().stream().map(usuarioMapper::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    public Proyecto toEntity(ProyectoRequestDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo(dto.getTitulo());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFin(dto.getFechaFin());
        proyecto.setEstado(dto.getEstado());
        return proyecto;
    }
}

