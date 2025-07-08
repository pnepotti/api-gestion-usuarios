package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.RolRequestDTO;
import com.linsi.gestionusuarios.dto.RolResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public RolResponseDTO crearRol(RolRequestDTO rolDto) {
        if (rolRepository.existsByNombreIgnoreCase(rolDto.getNombre())) {
            throw new ConflictException("Ya existe un rol con el nombre: " + rolDto.getNombre());
        }
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(rolDto.getNombre());
        Rol rolGuardado = rolRepository.save(nuevoRol);
        return convertToDto(rolGuardado);
    }

    @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarRol(Long rolId) {
        if (!rolRepository.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }
        if (usuarioRepository.existsByRolId(rolId)) {
            throw new ConflictException("No se puede eliminar el rol porque está asignado a uno o más usuarios.");
        }
        rolRepository.deleteById(rolId);
    }

    private RolResponseDTO convertToDto(Rol rol) {
        RolResponseDTO dto = new RolResponseDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        return dto;
    }
}

