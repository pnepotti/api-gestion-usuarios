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
import com.linsi.gestionusuarios.model.Usuario;
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

    @Transactional(readOnly = true)
    public RolResponseDTO obtenerRol(Long rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));
        return rolMapper.toDto(rol);
    }

    @Transactional
    public RolResponseDTO actualizarRol(Long rolId, RolRequestDTO rolDto) {
        Rol rolExistente = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        if (rolRepository.existsByNombreIgnoreCaseAndIdNot(rolDto.getNombre(), rolId)) {
            throw new ConflictException("Ya existe un rol con el nombre: " + rolDto.getNombre());
        }

        rolExistente.setNombre(rolDto.getNombre());
        Rol rolActualizado = rolRepository.save(rolExistente);
        return rolMapper.toDto(rolActualizado);
    }

    @Transactional
    public void eliminarRol(Long rolId) {
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        for (Usuario usuario : new java.util.HashSet<>(rol.getUsuarios())) {
            usuario.setRol(null);
        }
        rol.getUsuarios().clear();

        rolRepository.delete(rol);
    }
}

