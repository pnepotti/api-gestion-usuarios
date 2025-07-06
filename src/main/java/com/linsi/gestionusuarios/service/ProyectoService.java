package com.linsi.gestionusuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.repository.ProyectoRepository;

@Service("proyectoService")
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    public boolean esDirector(Long proyectoId, Long usuarioId) {
        return proyectoRepository.findById(proyectoId)
            .map(p -> p.getDirector() != null && p.getDirector().getId().equals(usuarioId))
            .orElse(false);
    }

    public boolean esIntegrante(Long proyectoId, Long usuarioId) {
        return proyectoRepository.findById(proyectoId)
            .map(p -> p.getIntegrantes().stream().anyMatch(u -> u.getId().equals(usuarioId)))
            .orElse(false);
    }

    public boolean esDirectorOIntegrante(Long proyectoId, Long usuarioId) {
        return esDirector(proyectoId, usuarioId) || esIntegrante(proyectoId, usuarioId);
    }
}
