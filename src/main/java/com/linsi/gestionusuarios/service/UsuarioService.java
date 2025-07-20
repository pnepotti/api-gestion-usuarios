package com.linsi.gestionusuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.CambiarPasswordDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioUpdateDTO;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.MateriaMapper;
import com.linsi.gestionusuarios.mapper.ProyectoMapper;
import com.linsi.gestionusuarios.mapper.UsuarioMapper;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BecaService becaService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final MateriaMapper materiaMapper;
    private final ProyectoMapper proyectoMapper;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getUsuarios(String dni, String rol, String nombre, String apellido) {
        List<Usuario> usuarios;
        if (dni != null && !dni.isEmpty()) {
            usuarios = usuarioRepository.findByDni(dni).map(List::of).orElse(List.of());
        } else if (rol != null && !rol.isEmpty()) {
            usuarios = usuarioRepository.findByRolNombre(rol);
        } else if (nombre != null && !nombre.isEmpty() && apellido != null && !apellido.isEmpty()) {
            usuarios = usuarioRepository.findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(nombre, apellido);
        } else {
            usuarios = usuarioRepository.findAll();
        }
        return usuarios.stream().map(usuarioMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        desvincularDeRol(usuario);
        desvincularDeMaterias(usuario);
        desvincularDeProyectosComoIntegrante(usuario);
        desvincularDeProyectosComoDirector(usuario);

        becaService.eliminarBecasDeUsuario(usuario.getId());
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long usuarioId, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setDni(dto.getDni());
        usuario.setLegajo(dto.getLegajo() != null ? dto.getLegajo() : null);
        usuario.setEmail(dto.getEmail());
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(updatedUsuario);
    }

    @Transactional
    public void cambiarPassword(Long usuarioId, CambiarPasswordDTO dto, Authentication authentication) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!isAdmin && !passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }
        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void asignarRol(Long usuarioId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        
        Rol nuevoRol = rolRepository.findById(rolId)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        if (usuario.getRol() != null) {
            usuario.getRol().getUsuarios().remove(usuario);
        }

        usuario.setRol(nuevoRol);
        nuevoRol.getUsuarios().add(usuario);
    }

    @Transactional(readOnly = true)
    public List<BecaResponseDTO> getBecasByUsuario(Long usuarioId) {
        return becaService.listarBecasPorUsuario(usuarioId);
    }

    @Transactional
    public BecaResponseDTO crearBecaParaUsuario(Long usuarioId, BecaRequestDTO becaDto) {
        return becaService.crearBecaParaUsuario(usuarioId, becaDto);
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> getProyectosByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        
        return usuario.getProyectos().stream()
            .map(proyectoMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> getMateriasByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return usuario.getMaterias().stream()
            .map(materiaMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioDto(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuarioMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }

    // --- Métodos privados de ayuda para la desvinculación ---

    private void desvincularDeRol(Usuario usuario) {
        if (usuario.getRol() != null) {
            usuario.getRol().getUsuarios().remove(usuario);
        }
    }

    private void desvincularDeMaterias(Usuario usuario) {
        // Se crea una copia del Set para iterar y evitar ConcurrentModificationException
        new java.util.HashSet<>(usuario.getMaterias()).forEach(materia -> 
            materia.getIntegrantes().remove(usuario)
        );
        usuario.getMaterias().clear();
    }

    private void desvincularDeProyectosComoIntegrante(Usuario usuario) {
        new java.util.HashSet<>(usuario.getProyectos()).forEach(proyecto -> 
            proyecto.getIntegrantes().remove(usuario)
        );
        usuario.getProyectos().clear();
    }

    private void desvincularDeProyectosComoDirector(Usuario usuario) {
        new java.util.HashSet<>(usuario.getProyectosDirigidos()).forEach(proyecto -> 
            proyecto.setDirector(null)
        );
        usuario.getProyectosDirigidos().clear();
    }
}
