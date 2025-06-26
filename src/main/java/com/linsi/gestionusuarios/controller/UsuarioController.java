package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.linsi.gestionusuarios.dto.AsignarRolDTO;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

   // @PreAuthorize("hasAnyRole('BECARIO', 'DOCENTE', 'ADMINISTRADOR')")
   @GetMapping("/me")
   public Usuario perfilActual(Authentication authentication) {
       return (Usuario) authentication.getPrincipal();
   }

 

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        if (!usuarioRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepo.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado");
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario actualizacion) {
        return usuarioRepo.findById(id)
                .map(u -> {
                    u.setNombre(actualizacion.getNombre());
                    u.setApellido(actualizacion.getApellido());
                    u.setEmail(actualizacion.getEmail());
                    u.setRol(null);
                    usuarioRepo.save(u);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/asignar-rol")
    public ResponseEntity<?> asignarRol(@RequestBody AsignarRolDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(dto.getUsuarioId());
        Optional<Rol> rolOpt = rolRepo.findById(dto.getRolId());

        if (usuarioOpt.isEmpty() || rolOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario o Rol no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setRol(rolOpt.get());
        usuarioRepo.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMINISTRADOR')")
    @PutMapping("/me/{id}")
    public ResponseEntity<Usuario> actualizarPropioPerfil(@PathVariable Long id, @RequestBody Usuario actualizacion) {
        return usuarioRepo.findById(id)
                .map(u -> {
                    u.setNombre(actualizacion.getNombre());
                    u.setApellido(actualizacion.getApellido());
                    u.setEmail(actualizacion.getEmail());
                    usuarioRepo.save(u);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
