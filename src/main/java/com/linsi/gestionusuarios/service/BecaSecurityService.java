package com.linsi.gestionusuarios.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.BecaRepository;

import lombok.RequiredArgsConstructor;

@Service("becaSecurity") 
@RequiredArgsConstructor
public class BecaSecurityService {

    private final BecaRepository becaRepository;

    /**
     * @param becaId El ID de la beca a verificar.
     * @param authentication El objeto de autenticación del usuario actual.
     * @return true si el usuario es el propietario, false en caso contrario.
     */
    public boolean esPropietario(Long becaId, Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        Long usuarioId = usuarioAutenticado.getId();

        return becaRepository.findById(becaId)
                .map(beca -> beca.getUsuario().getId().equals(usuarioId))
                .orElse(false);
    }
}
