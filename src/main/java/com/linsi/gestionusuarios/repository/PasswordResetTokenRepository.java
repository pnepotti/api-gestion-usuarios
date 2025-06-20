package com.linsi.gestionusuarios.repository;

import com.linsi.gestionusuarios.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUsuarioId(Long usuarioId);
}