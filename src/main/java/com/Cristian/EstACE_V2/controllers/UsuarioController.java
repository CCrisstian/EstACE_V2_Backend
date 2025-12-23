package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/test")
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }
}