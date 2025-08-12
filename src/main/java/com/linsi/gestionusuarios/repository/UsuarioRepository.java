package com.linsi.gestionusuarios.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.linsi.gestionusuarios.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByDni(String dni);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.rol WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
    
    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findByRolNombre(String rol, Pageable pageable);

    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findByAreaNombre(String area, Pageable pageable);

    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(String nombre, String apellido, Pageable pageable);
    
    boolean existsByRolId(Long rolId);
    boolean existsByLegajo(String legajo);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByLegajoAndIdNot(String legajo, Long id);
    boolean existsByDniAndIdNot(String dni, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Override
    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findByProyectos_Id(Long proyectoId, Pageable pageable);

    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findByMaterias_Id(Long materiaId, Pageable pageable);

    @EntityGraph(attributePaths = "rol")
    Page<Usuario> findByArea_Id(Long areaId, Pageable pageable);
}
