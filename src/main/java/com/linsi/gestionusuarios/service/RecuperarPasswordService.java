package com.linsi.gestionusuarios.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.exception.InvalidTokenException;
import com.linsi.gestionusuarios.model.PasswordResetToken;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.PasswordResetTokenRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperarPasswordService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    private void enviarEmail(String email, String enlace) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recuperación de contraseña");
        message.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" + enlace);
        mailSender.send(message);
    }

    @Transactional
    public void solicitarRecuperacion(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return;
        }
        Usuario usuario = usuarioOpt.get();

        tokenRepo.deleteByUsuarioId(usuario.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                null,
                token,
                usuario,
                LocalDateTime.now().plusHours(1)); 
        tokenRepo.save(resetToken);

        String enlace = frontendUrl + "/restablecer-password?token=" + token;
        enviarEmail(usuario.getEmail(), enlace);
    }

    @Transactional
    public void restablecerPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido o no encontrado."));

        if (resetToken.getExpiracion().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken); 
            throw new InvalidTokenException("El token ha expirado.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepo.save(usuario);

        tokenRepo.delete(resetToken);
    }
}
