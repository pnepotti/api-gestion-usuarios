package com.linsi.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.linsi.gestionusuarios.model.Beca;
import java.util.List;

public interface BecaRepository extends JpaRepository<Beca, Long> {
    List<Beca> findByUsuarioId(Long usuarioId);
}
