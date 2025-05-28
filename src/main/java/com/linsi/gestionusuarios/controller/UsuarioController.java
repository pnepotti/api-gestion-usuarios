package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // solo ADMIN puede acceder
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me")
    public Usuario perfilActual(Authentication authentication) {
        return (Usuario) authentication.getPrincipal();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        if (!usuarioRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepo.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario actualizacion) {
        return usuarioRepo.findById(id)
                .map(u -> {
                    u.setNombre(actualizacion.getNombre());
                    u.setEmail(actualizacion.getEmail());
                    u.setRol(actualizacion.getRol());
                    usuarioRepo.save(u);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/me/{id}")
    public ResponseEntity<Usuario> actualizarPropioPerfil(@PathVariable Long id, @RequestBody Usuario actualizacion) {
        return usuarioRepo.findById(id)
                .map(u -> {
                    u.setNombre(actualizacion.getNombre());
                    usuarioRepo.save(u);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
