package com.linsi.gestionusuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linsi.gestionusuarios.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {    
    Optional<Rol> findByNombre(String nombre);  
    boolean existsByNombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
