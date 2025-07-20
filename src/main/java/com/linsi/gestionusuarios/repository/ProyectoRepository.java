package com.linsi.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.linsi.gestionusuarios.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    @Query("SELECT COUNT(p) > 0 FROM Proyecto p WHERE p.id = :proyectoId AND p.director.id = :usuarioId")
    boolean esDirector(@Param("proyectoId") Long proyectoId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(p) > 0 FROM Proyecto p JOIN p.integrantes i WHERE p.id = :proyectoId AND i.id = :usuarioId")
    boolean esIntegrante(@Param("proyectoId") Long proyectoId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(p) > 0 FROM Proyecto p LEFT JOIN p.integrantes i WHERE p.id = :proyectoId AND (p.director.id = :usuarioId OR i.id = :usuarioId)")
    boolean esDirectorOIntegrante(@Param("proyectoId") Long proyectoId, @Param("usuarioId") Long usuarioId);

}
