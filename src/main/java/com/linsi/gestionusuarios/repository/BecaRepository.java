package com.linsi.gestionusuarios.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.linsi.gestionusuarios.model.Beca;

public interface BecaRepository extends JpaRepository<Beca, Long> {
    List<Beca> findByUsuarioId(Long usuarioId);

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.rol"})
    Page<Beca> findAll(Pageable pageable);
}
