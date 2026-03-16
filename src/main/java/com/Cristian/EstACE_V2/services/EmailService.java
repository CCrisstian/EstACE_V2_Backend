package com.Cristian.EstACE_V2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void enviarCorreoRecuperacion(String destinatario, String token) {

        String urlRecuperacion = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de Contraseña - EstACE V2.0");
        mensaje.setText("Hola,\n\n"
                + "Hemos recibido una solicitud para restablecer tu contraseña.\n"
                + "Para crear una nueva contraseña, haz clic en el siguiente enlace:\n\n"
                + urlRecuperacion + "\n\n"
                + "Este enlace expirará en 15 minutos.\n"
                + "Si no solicitaste este cambio, ignora este correo.");

        mailSender.send(mensaje);
    }
}