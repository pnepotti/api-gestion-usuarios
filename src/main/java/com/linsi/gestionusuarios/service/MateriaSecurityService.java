package com.linsi.gestionusuarios.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.MateriaRepository;

import lombok.RequiredArgsConstructor;

@Service("materiaSecurity")
@RequiredArgsConstructor
public class MateriaSecurityService {

    private final MateriaRepository materiaRepository;

    /**
     * @param materiaId El ID de la materia a verificar.
     * @param authentication El objeto de autenticación del usuario actual.
     * @return true si el usuario es integrante, false en caso contrario.
     */
    public boolean esIntegrante(Long materiaId, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuarioAutenticado.getId();

        return materiaRepository.findById(materiaId)
            .map(materia -> materia.getIntegrantes().stream()
                .anyMatch(integrante -> integrante.getId().equals(usuarioId)))
            .orElse(false); 
    }
}
