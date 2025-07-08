package com.linsi.gestionusuarios.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.linsi.gestionusuarios.exception.InvalidTokenException;
import com.linsi.gestionusuarios.model.PasswordResetToken;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.PasswordResetTokenRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecuperarPasswordService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;


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

        // Elimina tokens anteriores
        tokenRepo.deleteByUsuarioId(usuario.getId());

        // Genera token y guarda
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                null,
                token,
                usuario,
                LocalDateTime.now().plusHours(1)); // Token válido por 1 hora
        tokenRepo.save(resetToken);

        // Enlace de recuperación (ajusta la URL según tu frontend)
        String enlace = "https://tusitio.com/restablecer?token=" + token;
        enviarEmail(usuario.getEmail(), enlace);
    }

    @Transactional
    public void restablecerPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido o no encontrado."));

        if (resetToken.getExpiracion().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken); // Limpiar token expirado
            throw new InvalidTokenException("El token ha expirado.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepo.save(usuario);

        // Elimina el token usado
        tokenRepo.delete(resetToken);
    }
}
