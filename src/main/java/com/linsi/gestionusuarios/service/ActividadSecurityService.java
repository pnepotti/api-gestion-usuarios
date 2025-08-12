package com.linsi.gestionusuarios.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.ActividadRepository;

import lombok.RequiredArgsConstructor;

@Service("actividadSecurity")
@RequiredArgsConstructor
public class ActividadSecurityService {
    private final ActividadRepository actividadRepository;
    private final ProyectoSecurityService proyectoSecurityService;

    public boolean esResponsable(Long actividadId, Authentication authentication) {
        return actividadRepository.findById(actividadId)
                .map(actividad -> {
                    Usuario usuario = (Usuario) authentication.getPrincipal();
                    Long usuarioId = usuario.getId();

                    if (actividad.getProyecto() == null) {
                        return hasRole(authentication, "DOCENTE");
                    }
                    return proyectoSecurityService.esDirector(actividad.getProyecto().getId(), usuarioId);
                })
                .orElse(false);
    }

    public boolean esIntegranteDelProyecto(Long actividadId, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuarioAutenticado.getId();

        return actividadRepository.findById(actividadId)
            .map(actividad -> {
                if (actividad.getProyecto() == null) {
                    return false;
                }
                return actividad.getProyecto().getIntegrantes().stream()
                    .anyMatch(integrante -> integrante.getId().equals(usuarioId));
            })
            .orElse(false); // Si la actividad no existe, se deniega el acceso.
    }

    private boolean hasRole(Authentication authentication, String roleName) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + roleName));
    }
}        
