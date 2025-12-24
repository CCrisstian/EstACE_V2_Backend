package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.LoginRequest;
import com.Cristian.EstACE_V2.dtos.UsuarioResponse;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.security.JwtService;
import com.Cristian.EstACE_V2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager; // Verifica credenciales

    @Autowired
    private JwtService jwtService; // Crea el token

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Autenticamos con Spring Security (Lanzará excepción si falla)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLegajo().toString(),
                        loginRequest.getPassword()
                )
        );

        // 2. Si pasa, buscamos el usuario para obtener sus datos
        Usuario usuario = usuarioService.buscarPorLegajo(loginRequest.getLegajo());

        // 3. Generamos el Token
        String jwtToken = jwtService.generateToken(usuario.getUsername());

        // 4. Preparamos la respuesta
        UsuarioResponse response = new UsuarioResponse();
        response.setLegajo(usuario.getUsuLegajo());
        response.setDni(usuario.getUsuDni());
        response.setNombre(usuario.getUsuNom());
        response.setApellido(usuario.getUsuAp());
        response.setTipo(usuario.getUsuTipo());
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getUsuDni() == null || usuario.getUsuPass() == null) {
            return ResponseEntity.badRequest().body("DNI y Contraseña son obligatorios");
        }
        Usuario usuarioGuardado = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
    }
}