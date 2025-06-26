package com.linsi.gestionusuarios.controller;

import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolRepository rolRepo;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolRepo.save(rol));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<Rol> listarRoles() {
        return rolRepo.findAll();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        if (!rolRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rolRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
