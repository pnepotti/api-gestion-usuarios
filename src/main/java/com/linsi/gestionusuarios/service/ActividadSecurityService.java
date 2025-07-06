package com.linsi.gestionusuarios.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.model.Actividad;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.ActividadRepository;

@Service("actividadSecurity")
public class ActividadSecurityService {
    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private ProyectoService proyectoService;

    public boolean puedeModificar(Long actividadId, Authentication authentication) {
        Optional<Actividad> actividadOpt = actividadRepository.findById(actividadId);
        if (actividadOpt.isEmpty()) {
            return true; // El controlador se encargará del 404, aquí permitimos que continúe.
        }
        Actividad actividad = actividadOpt.get();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            return true;
        }

        Long usuarioId = ((Usuario) authentication.getPrincipal()).getId();

        if (actividad.getProyecto() == null) {
            return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"));
        } else {
            return proyectoService.esDirector(actividad.getProyecto().getId(), usuarioId);
        }
    }
}        

