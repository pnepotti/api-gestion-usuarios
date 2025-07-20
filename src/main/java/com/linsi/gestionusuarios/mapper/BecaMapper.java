package com.linsi.gestionusuarios.mapper;

import org.springframework.stereotype.Component;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.model.Beca;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BecaMapper {

    private final UsuarioMapper usuarioMapper;

    public BecaResponseDTO toDto(Beca beca) {
        BecaResponseDTO dto = new BecaResponseDTO();
        dto.setId(beca.getId());
        dto.setNombre(beca.getNombre());
        dto.setTipo(beca.getTipo());
        dto.setMonto(beca.getMonto());
        dto.setFechaInicio(beca.getFechaInicio());
        dto.setFechaFin(beca.getFechaFin());
        dto.setDuracion(beca.getDuracion());
        dto.setUsuario(usuarioMapper.toDto(beca.getUsuario()));
        return dto;
    }

    public Beca toEntity(BecaRequestDTO dto) {
        Beca beca = new Beca();
        beca.setNombre(dto.getNombre());
        beca.setTipo(dto.getTipo());
        beca.setMonto(dto.getMonto());
        beca.setFechaInicio(dto.getFechaInicio());
        beca.setFechaFin(dto.getFechaFin());
        beca.setDuracion(dto.getDuracion());
        return beca;
    }
}
