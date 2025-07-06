package com.linsi.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.linsi.gestionusuarios.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
   

}
