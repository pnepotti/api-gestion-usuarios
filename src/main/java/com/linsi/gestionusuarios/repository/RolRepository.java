package com.linsi.gestionusuarios.repository;

import com.linsi.gestionusuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {    
    Optional<Rol> findByNombre(String nombre);  
    boolean existsByNombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
