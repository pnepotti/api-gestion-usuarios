package com.linsi.gestionusuarios.repository;

import com.linsi.gestionusuarios.model.Materia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    @Override
    Page<Materia> findAll(Pageable pageable);
}
