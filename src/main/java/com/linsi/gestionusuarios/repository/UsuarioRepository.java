package com.linsi.gestionusuarios.repository;

import com.linsi.gestionusuarios.model.Usuario;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByDni(String dni);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.rol WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.rol r WHERE r.nombre = :rol")
    List<Usuario> findByRolNombre(@Param("rol") String rol);

    @EntityGraph(attributePaths = "rol")
    List<Usuario> findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(String nombre, String apellido);
    
    boolean existsByRolId(Long rolId);

    @Override
    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAll();
}
