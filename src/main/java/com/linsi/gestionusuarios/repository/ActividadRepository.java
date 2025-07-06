package com.linsi.gestionusuarios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.linsi.gestionusuarios.model.Actividad;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByProyectoId(Long proyectoId);
}
