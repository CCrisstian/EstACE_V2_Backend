package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.entities.Usuario;
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

    public Usuario buscarPorLegajo(Integer legajo) {
        return usuarioRepository.findByUsuLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con legajo: " + legajo));
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
}
