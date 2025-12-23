package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.LoginRequest;
import com.Cristian.EstACE_V2.dtos.UsuarioResponse;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.autenticar(loginRequest.getLegajo(), loginRequest.getPassword());

        if (usuario != null) {
            // Convertimos la Entidad a DTO para no enviar el password
            UsuarioResponse response = new UsuarioResponse();
            response.setLegajo(usuario.getUsuLegajo());
            response.setDni(usuario.getUsuDni());
            response.setNombre(usuario.getUsuNom());
            response.setApellido(usuario.getUsuAp());
            response.setTipo(usuario.getUsuTipo());

            return ResponseEntity.ok(response);
        } else {
            // Login fallido
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        // Validamos que no venga vacío (lógica básica)
        if (usuario.getUsuDni() == null || usuario.getUsuPass() == null) {
            return ResponseEntity.badRequest().body("DNI y Contraseña son obligatorios");
        }

        // Llamamos al servicio que ENCRIPTA y GUARDA
        Usuario usuarioGuardado = usuarioService.registrarUsuario(usuario);

        // Devolvemos el usuario creado (¡Ojo! Idealmente devuelve un DTO sin pass,
        // pero para probar ahora está bien).
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
    }
}