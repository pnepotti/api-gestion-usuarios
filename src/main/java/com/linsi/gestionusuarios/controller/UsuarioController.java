package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.model.Beca;
import com.linsi.gestionusuarios.model.Materia;
import com.linsi.gestionusuarios.model.Proyecto;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.BecaRepository;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.linsi.gestionusuarios.dto.AsignarRolDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.CambiarPasswordDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.MensajeResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioUpdateDTO;

import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Gestión de Usuarios", description = "API para la creación y gestión de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BecaRepository becaRepository; 

    private UsuarioResponseDTO convertUsuarioToDto(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
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
        // No incluido el usuario para evitar ciclos
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

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepo.findAll().stream()
                .map(this::convertUsuarioToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepo.deleteById(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long usuarioId, @Valid @RequestBody UsuarioUpdateDTO dto) {
        return usuarioRepo.findById(usuarioId)
                .map(u -> {
                    u.setNombre(dto.getNombre());
                    u.setApellido(dto.getApellido());
                    u.setEmail(dto.getEmail());
                    usuarioRepo.save(u);

                    return ResponseEntity.ok(convertUsuarioToDto(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public UsuarioResponseDTO perfilActual(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return convertUsuarioToDto(usuario);
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMINISTRADOR')")
    @PutMapping("/me/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> actualizarPropioPerfil(@PathVariable Long usuarioId, @Valid @RequestBody UsuarioUpdateDTO dto) {
        return usuarioRepo.findById(usuarioId)
                .map(u -> {
                    u.setNombre(dto.getNombre());
                    u.setApellido(dto.getApellido());
                    u.setEmail(dto.getEmail());
                    usuarioRepo.save(u);

                    return ResponseEntity.ok(convertUsuarioToDto(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMINISTRADOR')")
    @PutMapping("/{usuarioId}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long usuarioId,
            @Valid @RequestBody CambiarPasswordDTO dto,
            Authentication authentication) {

        return usuarioRepo.findById(usuarioId)
                .map(usuario -> {
                    // Si no es admin, validar password actual
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
                    if (!isAdmin && !passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
                        return ResponseEntity.badRequest().body(new MensajeResponseDTO("La contraseña actual es incorrecta"));
                    }
                    usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
                    usuarioRepo.save(usuario);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/buscar-por-nombre-apellido")
    public List<UsuarioResponseDTO> buscarUsuariosPorNombreApellido(
            @RequestParam String nombre,
            @RequestParam String apellido) {
        return usuarioRepo.findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(nombre, apellido)
                .stream()                
                .map(this::convertUsuarioToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/buscar-por-dni")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorDni(@RequestParam String dni) {
        return usuarioRepo.findByDni(dni)
                .map(this::convertUsuarioToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //ROLES

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/rol/{rol}")
    public List<UsuarioResponseDTO> listarUsuariosPorRol(@PathVariable String rol) {
        return usuarioRepo.findByRolNombre(rol).stream()
                .map(this::convertUsuarioToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{usuarioId}/rol")
    public ResponseEntity<?> asignarRol(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AsignarRolDTO dto) {
        
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Usuario no encontrado con ID: " + usuarioId));
        }

        Optional<Rol> rolOpt = rolRepo.findById(dto.getRolId());
        if (rolOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Rol no encontrado con ID: " + dto.getRolId()));
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setRol(rolOpt.get());
        usuarioRepo.save(usuario);
        return ResponseEntity.ok(convertUsuarioToDto(usuario));
    }

    //BECAS

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{usuarioId}/becas")
    public List<BecaResponseDTO> listarBecasDeUsuario(@PathVariable Long usuarioId) {
        return becaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertBecaToDto)
                .toList();
    }

    //PROYECTOS

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{usuarioId}/proyectos")
    public ResponseEntity<List<ProyectoResponseDTO>> listarProyectosDeUsuario(@PathVariable Long usuarioId) {
        return usuarioRepo.findById(usuarioId)
                .map(usuario -> {
                    List<ProyectoResponseDTO> proyectos = usuario.getProyectos().stream()
                            .map(this::convertProyectoToDto)
                            .toList();
                    return ResponseEntity.ok(proyectos);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //MATERIAS

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{usuarioId}/materias")
    public ResponseEntity<List<MateriaResponseDTO>> listarMateriasDeUsuario(@PathVariable Long usuarioId) {
        return usuarioRepo.findById(usuarioId)
                .map(usuario -> {
                    List<MateriaResponseDTO> materias = usuario.getMaterias().stream()
                            .map(this::convertMateriaToDto)
                            .toList();
                    return ResponseEntity.ok(materias);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
