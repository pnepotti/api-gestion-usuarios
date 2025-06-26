package com.linsi.gestionusuarios.repository;

import com.linsi.gestionusuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    
    // Método para encontrar un rol por su nombre
    Optional<Rol> findByNombre(String nombre);
    
    // Método para verificar si un rol existe por su nombre
    boolean existsByNombre(String nombre);

}
