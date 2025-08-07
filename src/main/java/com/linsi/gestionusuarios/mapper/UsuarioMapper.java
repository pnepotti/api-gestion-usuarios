package com.linsi.gestionusuarios.mapper;

import org.springframework.stereotype.Component;

import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.model.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setDni(usuario.getDni());
        dto.setLegajo(usuario.getLegajo() != null ? usuario.getLegajo() : null);
        dto.setTelefono(usuario.getTelefono() != null ? usuario.getTelefono() : null);
        dto.setDireccion(usuario.getDireccion() != null ? usuario.getDireccion() : null);
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }
}
