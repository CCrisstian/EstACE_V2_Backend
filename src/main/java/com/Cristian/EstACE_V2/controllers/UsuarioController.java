package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.config.JwtService;
import com.Cristian.EstACE_V2.dtos.*;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public record AvatarUpdateRequest(String avatarUrl) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Autenticar usando el EMAIL
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        Usuario usuario = usuarioService.buscarPorEmail(loginRequest.getEmail());

        // 3. Generar Token
        String jwtToken = jwtService.generateToken(usuario);

        // 4. Preparar respuesta
        UsuarioResponse response = new UsuarioResponse();
        response.setLegajo(usuario.getUsuLegajo());
        response.setDni(usuario.getUsuDni());
        response.setNombre(usuario.getUsuNom());
        response.setApellido(usuario.getUsuAp());
        response.setTipo(usuario.getUsuTipo());

        // --- AGREGAMOS EMAIL Y TELÉFONO A LA RESPUESTA ---
        response.setEmail(usuario.getUsuEmail());
        response.setTelefono(usuario.getUsuTelefono());

        response.setToken(jwtToken);
        response.setAvatarUrl(usuario.getUsuAvatarUrl());

        return ResponseEntity.ok(response);
    }

    // GET: Ver mi propio perfil
    @GetMapping("/perfil")
    public ResponseEntity<?> verMiPerfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajoString = auth.getName();
        Integer legajo = Integer.parseInt(legajoString);

        Usuario usuario = usuarioService.buscarPorLegajo(legajo);

        UsuarioResponse response = new UsuarioResponse();
        response.setLegajo(usuario.getUsuLegajo());
        response.setDni(usuario.getUsuDni());
        response.setNombre(usuario.getUsuNom());
        response.setApellido(usuario.getUsuAp());
        response.setTipo(usuario.getUsuTipo());

        response.setEmail(usuario.getUsuEmail());
        response.setTelefono(usuario.getUsuTelefono());
        response.setDireccion(usuario.getUsuDireccion());

        response.setAvatarUrl(usuario.getUsuAvatarUrl());

        return ResponseEntity.ok(response);
    }

    // PUT: Editar mi propio perfil
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarMiPerfil(@Valid @RequestBody UsuarioUpdateRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Integer legajo = Integer.parseInt(auth.getName());

            Usuario usuarioActualizado = usuarioService.actualizarPerfil(legajo, request);

            UsuarioResponse response = new UsuarioResponse();
            response.setLegajo(usuarioActualizado.getUsuLegajo());
            response.setDni(usuarioActualizado.getUsuDni());
            response.setNombre(usuarioActualizado.getUsuNom());
            response.setApellido(usuarioActualizado.getUsuAp());
            response.setTipo(usuarioActualizado.getUsuTipo());

            response.setEmail(usuarioActualizado.getUsuEmail());
            response.setTelefono(usuarioActualizado.getUsuTelefono());
            response.setDireccion(usuarioActualizado.getUsuDireccion());

            response.setAvatarUrl(usuarioActualizado.getUsuAvatarUrl());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{legajo}/avatar")
    public ResponseEntity<?> actualizarAvatar(@PathVariable Integer legajo, @RequestBody AvatarUpdateRequest request) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarAvatar(legajo, request.avatarUrl());
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            usuarioService.solicitarRecuperacionPassword(request.getEmail());
            // Devolvemos un JSON simple con un mensaje de éxito
            return ResponseEntity.ok(Map.of("message", "Te hemos enviado un correo con las instrucciones."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            usuarioService.restablecerPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "¡Contraseña actualizada exitosamente!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}