package com.linsi.gestionusuarios.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.linsi.gestionusuarios.model.Actividad;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    Page<Actividad> findByProyectoId(Long proyectoId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"proyecto"})
    Page<Actividad> findAll(Pageable pageable);
}
