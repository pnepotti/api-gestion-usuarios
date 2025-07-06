package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.dto.RolRequestDTO;
import com.linsi.gestionusuarios.dto.RolResponseDTO;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Gestión de Roles", description = "API para la creación y gestión de roles de usuario")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private RolResponseDTO convertToDto(Rol rol) {
        RolResponseDTO dto = new RolResponseDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        return dto;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<RolResponseDTO> crearRol(@Valid @RequestBody RolRequestDTO rolDto) {
        if (rolRepository.existsByNombreIgnoreCase(rolDto.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(rolDto.getNombre());
        Rol rolGuardado = rolRepository.save(nuevoRol);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(rolGuardado));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
     public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{rolId}")
     public ResponseEntity<Void> eliminarRol(@PathVariable Long rolId) {
        if (!rolRepository.existsById(rolId)) {
            return ResponseEntity.notFound().build();
        }
         // Verificación de dependencias: no se puede borrar un rol si está en uso.
        if (usuarioRepository.existsByRolId(rolId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict
        }
        rolRepository.deleteById(rolId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
