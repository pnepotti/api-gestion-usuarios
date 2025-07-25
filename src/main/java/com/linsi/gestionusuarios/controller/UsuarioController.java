package com.linsi.gestionusuarios.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.CambiarPasswordDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioUpdateDTO;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Gestión de Usuarios", description = "API para la creación y gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            Pageable pageable) {
        return ResponseEntity.ok(usuarioService.getUsuarios(dni, rol, nombre, apellido, pageable));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.id")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.getUsuarioDto(id);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public UsuarioResponseDTO perfilActual(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return usuarioService.getUsuarioDto(usuario.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> actualizarPropioPerfil(@Valid @RequestBody UsuarioUpdateDTO dto, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(usuario.getId(), dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordDTO dto,
            Authentication authentication) {

        usuarioService.cambiarPassword(id, dto, authentication);
        return ResponseEntity.noContent().build();
    }

    //ROLES

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/rol/{rolId}")
    public ResponseEntity<Void> asignarRol(
            @PathVariable Long id,
                @PathVariable Long rolId) {
        usuarioService.asignarRol(id, rolId);
        return ResponseEntity.noContent().build();
    }

    //BECAS

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}/becas")
    public ResponseEntity<List<BecaResponseDTO>> listarBecasDeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getBecasByUsuario(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{id}/becas")
    public ResponseEntity<BecaResponseDTO> crearBecaParaUsuario(
            @PathVariable Long id,
            @Valid @RequestBody BecaRequestDTO becaDto) {
        BecaResponseDTO becaCreada = usuarioService.crearBecaParaUsuario(id, becaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(becaCreada);
    }

    //PROYECTOS

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}/proyectos")
    public ResponseEntity<List<ProyectoResponseDTO>> listarProyectosDeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getProyectosByUsuario(id));
    }

    //MATERIAS

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{id}/materias")
    public ResponseEntity<List<MateriaResponseDTO>> listarMateriasDeUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getMateriasByUsuario(id));
    }
}
