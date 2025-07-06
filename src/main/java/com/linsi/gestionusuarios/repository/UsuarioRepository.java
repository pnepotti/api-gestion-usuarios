package com.linsi.gestionusuarios.repository;

import com.linsi.gestionusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRolNombre(String nombre);
    List<Usuario> findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(String nombre, String apellido);
    boolean existsByRolId(Long rolId);
}
