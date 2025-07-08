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
        return proyectoRepository.findById(proyectoId)
                .map(proyecto -> proyecto.getDirector() != null && proyecto.getDirector().getId().equals(usuarioId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean esIntegrante(Long proyectoId, Long usuarioId) {
        return proyectoRepository.findById(proyectoId)
            .map(p -> p.getIntegrantes().stream().anyMatch(u -> u.getId().equals(usuarioId)))
            .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean esDirectorOIntegrante(Long proyectoId, Long usuarioId) {
        // Reutiliza los otros métodos para mantener el código limpio (DRY)
        return esDirector(proyectoId, usuarioId) || esIntegrante(proyectoId, usuarioId);
    }
}
