package com.linsi.gestionusuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.RolRequestDTO;
import com.linsi.gestionusuarios.dto.RolResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.RolMapper;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Transactional
    public RolResponseDTO crearRol(RolRequestDTO rolDto) {
        if (rolRepository.existsByNombreIgnoreCase(rolDto.getNombre())) {
            throw new ConflictException("Ya existe un rol con el nombre: " + rolDto.getNombre());
        }
        Rol nuevoRol = rolMapper.toEntity(rolDto);
        Rol rolGuardado = rolRepository.save(nuevoRol);
        return rolMapper.toDto(rolGuardado);
    }

    @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarRol(Long rolId) {
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        for (com.linsi.gestionusuarios.model.Usuario usuario : new java.util.HashSet<>(rol.getUsuarios())) {
            usuario.setRol(null);
        }
        rol.getUsuarios().clear();

        rolRepository.delete(rol);
    }
}

