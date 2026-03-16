package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.entities.PasswordResetToken;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.repositories.PasswordResetTokenRepository;
import com.Cristian.EstACE_V2.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Cristian.EstACE_V2.dtos.UsuarioUpdateRequest;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Herramienta BCrypt

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public Usuario autenticar(String email, String password) {
        // 1. Buscamos el usuario por Email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuEmail(email);

        // 2. Si existe, verificamos la contraseña
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getUsuPass())) {
                return usuario; // Login exitoso
            }
        }
        return null;
    }

    public Usuario buscarPorLegajo(Integer legajo) {
        return usuarioRepository.findByUsuLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con legajo: " + legajo));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByUsuEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + email));
    }

    @Transactional
    public Usuario actualizarPerfil(Integer legajo, UsuarioUpdateRequest request) {
        // 1. Buscar al usuario en la BD
        Usuario usuario = buscarPorLegajo(legajo);

        // --- VALIDACIONES DE NEGOCIO ---

        // 2. Validar que el DNI no esté vacío y sea numérico (básico)
        if (request.getDni() == null || request.getDni() <= 0) {
            throw new RuntimeException("El DNI es inválido");
        }

        if (request.getNombre() == null
                || request.getNombre().trim().isEmpty()
                || request.getNombre().matches("\\d+")) {
            throw new RuntimeException("El Nombre es inválido");
        }

        if (request.getApellido() == null
                || request.getApellido().trim().isEmpty()
                || request.getApellido().matches("\\d+")) {
            throw new RuntimeException("El Apellido es inválido");
        }

        // 3. Validar UNICIDAD del DNI en BD
        boolean dniExiste = usuarioRepository.existsByUsuDniAndUsuLegajoNot(request.getDni(), legajo);
        if (dniExiste) {
            throw new RuntimeException("El DNI ingresado ya pertenece a otro usuario.");
        }

        // --- ACTUALIZACIÓN DE DATOS COMUNES ---
        usuario.setUsuDni(request.getDni());
        usuario.setUsuNom(request.getNombre());
        usuario.setUsuAp(request.getApellido());
        usuario.setUsuEmail(request.getEmail());
        usuario.setUsuTelefono(request.getTelefono());
        usuario.setUsuDireccion(request.getDireccion());

        // --- ACTUALIZACIÓN DE CONTRASEÑA (Solo Dueño) ---
        // Verificamos si envió un password nuevo Y si es Dueño
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if ("Dueño".equalsIgnoreCase(usuario.getUsuTipo())) {
                String passCifrada = passwordEncoder.encode(request.getPassword());
                usuario.setUsuPass(passCifrada);
            } else {
                throw new RuntimeException("Solo los Dueños pueden cambiar la contraseña aquí.");
            }
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarAvatar(Integer legajo, String urlAvatar) {
        Usuario usuario = usuarioRepository.findById(legajo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUsuAvatarUrl(urlAvatar);
        return usuarioRepository.save(usuario);
    }

    // --- MÉTODOS PARA RECUPERAR CONTRASEÑA ---

    @Transactional
    public void solicitarRecuperacionPassword(String email) {
        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByUsuEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe un usuario registrado con ese correo."));

        // 2. Generar un Token seguro y único (UUID)
        String tokenStr = java.util.UUID.randomUUID().toString();

        // 3. Buscar si ya existe un token previo para este usuario
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByUsuario_UsuLegajo(usuario.getUsuLegajo());

        if (tokenOpt.isPresent()) {
            // Si ya existe, lo actualizamos (Evita el error de bloqueo de Hibernate)
            PasswordResetToken tokenExistente = tokenOpt.get();
            tokenExistente.setToken(tokenStr);
            tokenExistente.setFechaExpiracion(java.time.LocalDateTime.now().plusMinutes(15));
            tokenRepository.save(tokenExistente);
        } else {
            // Si no existe, creamos uno nuevo
            PasswordResetToken nuevoToken = new PasswordResetToken(tokenStr, usuario);
            tokenRepository.save(nuevoToken);
        }

        // 4. Enviar el correo
        emailService.enviarCorreoRecuperacion(usuario.getUsuEmail(), tokenStr);
    }

    @Transactional
    public void restablecerPassword(String tokenStr, String nuevaPassword) {
        // 1. Buscar el token en la BD
        PasswordResetToken token = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new RuntimeException("El enlace de recuperación es inválido."));

        // 2. Validar que no esté expirado (pasaron más de 15 min)
        if (token.isExpirado()) {
            tokenRepository.delete(token); // Lo borramos porque ya no sirve
            throw new RuntimeException("El enlace ha expirado. Por favor, solicita uno nuevo.");
        }

        // 3. Cambiar la contraseña
        Usuario usuario = token.getUsuario();
        usuario.setUsuPass(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // 4. Eliminar el token usado para que no se pueda volver a usar
        tokenRepository.delete(token);
    }
}
