package com.linsi.gestionusuarios.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.repository.ProyectoRepository;

import lombok.RequiredArgsConstructor;

@Service("proyectoSecurity")
@RequiredArgsConstructor
public class ProyectoSecurityService {
    private final ProyectoRepository proyectoRepository;

    @Transactional(readOnly = true)
    public boolean esDirector(Long proyectoId, Long usuarioId) {
        return proyectoRepository.esDirector(proyectoId, usuarioId);
    }

    @Transactional(readOnly = true)
    public boolean esIntegrante(Long proyectoId, Long usuarioId) {
        return proyectoRepository.esIntegrante(proyectoId, usuarioId);
    }

    @Transactional(readOnly = true)
    public boolean esDirectorOIntegrante(Long proyectoId, Long usuarioId) {
        return esDirector(proyectoId, usuarioId) || esIntegrante(proyectoId, usuarioId);
    }
}
