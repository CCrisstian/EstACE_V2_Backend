package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.config.JwtService;
import com.Cristian.EstACE_V2.dtos.LoginRequest;
import com.Cristian.EstACE_V2.dtos.UsuarioResponse;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.Cristian.EstACE_V2.dtos.UsuarioUpdateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // DTO interno para recibir solo la URL
    record AvatarUpdateRequest(String avatarUrl) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLegajo().toString(),
                        loginRequest.getPassword()
                )
        );

        // 2. Buscar usuario
        Usuario usuario = usuarioService.buscarPorLegajo(loginRequest.getLegajo());

        // 3. Generar Token
        String jwtToken = jwtService.generateToken(usuario);

        // 4. Preparar respuesta
        UsuarioResponse response = new UsuarioResponse();
        response.setLegajo(usuario.getUsuLegajo());
        response.setDni(usuario.getUsuDni());
        response.setNombre(usuario.getUsuNom());
        response.setApellido(usuario.getUsuAp());
        response.setTipo(usuario.getUsuTipo());
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
}