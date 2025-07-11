package com.linsi.gestionusuarios.service;

import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.CambiarPasswordDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioUpdateDTO;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.model.Beca;
import com.linsi.gestionusuarios.model.Materia;
import com.linsi.gestionusuarios.model.Proyecto;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.BecaRepository;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BecaRepository becaRepository;
    private final PasswordEncoder passwordEncoder;

        // --- Métodos de Lógica de Negocio ---

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
        return usuarios.stream().map(this::convertUsuarioToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        usuarioRepository.deleteById(usuarioId);
    }

    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long usuarioId, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setDni(dto.getDni());
        usuario.setEmail(dto.getEmail());
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return convertUsuarioToDto(updatedUsuario);
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
        
        Rol rol = rolRepository.findById(rolId)
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<BecaResponseDTO> getBecasByUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        return becaRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(this::convertBecaToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> getProyectosByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        
        return usuario.getProyectos().stream()
            .map(this::convertProyectoToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> getMateriasByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return usuario.getMaterias().stream()
            .map(this::convertMateriaToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioDto(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(this::convertUsuarioToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }

    // --- Métodos de Conversión a DTO (Mappers) ---

    private UsuarioResponseDTO convertUsuarioToDto(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setDni(usuario.getDni());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }

    private BecaResponseDTO convertBecaToDto(Beca beca) {
        BecaResponseDTO dto = new BecaResponseDTO();
        dto.setId(beca.getId());
        dto.setNombre(beca.getNombre());
        dto.setTipo(beca.getTipo());
        dto.setMonto(beca.getMonto());
        dto.setFechaInicio(beca.getFechaInicio());
        dto.setFechaFin(beca.getFechaFin());
        dto.setDuracion(beca.getDuracion());
        return dto;
    }

    private ProyectoResponseDTO convertProyectoToDto(Proyecto proyecto) {
        ProyectoResponseDTO dto = new ProyectoResponseDTO();
        dto.setId(proyecto.getId());
        dto.setTitulo(proyecto.getTitulo());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFin(proyecto.getFechaFin());
        dto.setEstado(proyecto.getEstado());
        return dto;
    }

    private MateriaResponseDTO convertMateriaToDto(Materia materia) {
        MateriaResponseDTO dto = new MateriaResponseDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setCodigo(materia.getCodigo());
        dto.setAnio(materia.getAnio());
        dto.setDescripcion(materia.getDescripcion());
        return dto;
    }


}
