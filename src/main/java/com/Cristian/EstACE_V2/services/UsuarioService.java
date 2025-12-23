package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Herramienta BCrypt

    public Usuario autenticar(Integer legajo, String password) {
        // 1. Buscamos el usuario por Legajo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuLegajo(legajo);

        // 2. Si existe, verificamos la contraseña
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // 3. VERIFICACIÓN CON BCRYPT
            // Primer argumento: contraseña en texto plano (la que viene del Postman)
            // Segundo argumento: contraseña encriptada (la que está en la BD)
            if (passwordEncoder.matches(password, usuario.getUsuPass())) {
                return usuario; // Login exitoso
            }
        }

        // 3. Si no existe o la contraseña está mal, retornamos null
        return null;
    }

    // Método para registrar un Usuario
    @Transactional
    public Usuario registrarUsuario(Usuario usuarioNuevo) {

        // 1. Obtenemos la contraseña en texto plano (ej: "hola123")
        String passSinCifrar = usuarioNuevo.getUsuPass();

        // 2. La encriptamos (ej: se convierte en "$2a$10$R9h/cIPz...")
        String passCifrada = passwordEncoder.encode(passSinCifrar);

        // 3. Reemplazamos la contraseña original por la cifrada en el objeto
        usuarioNuevo.setUsuPass(passCifrada);

        // 4. Guardamos en la BD. Supabase solo verá el hash, nunca la real.
        return usuarioRepository.save(usuarioNuevo);
    }
}
