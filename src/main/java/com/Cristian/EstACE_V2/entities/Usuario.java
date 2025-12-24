package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder // Útil para construir objetos rápido
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_legajo")
    private Integer usuLegajo;

    @Column(name = "usu_dni", unique = true)
    private Integer usuDni;

    @Column(name = "usu_pass", length = 255)
    private String usuPass;

    @Column(name = "usu_ap", length = 45)
    private String usuAp;

    @Column(name = "usu_nom", length = 45)
    private String usuNom;

    @Column(name = "usu_tipo", length = 8)
    private String usuTipo;

    // --- IMPLEMENTACIÓN DE USERDETAILS (SEGURIDAD) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertimos "usuTipo" (Dueño/Playero) en un Rol de Spring Security
        return List.of(new SimpleGrantedAuthority(usuTipo));
    }

    @Override
    public String getPassword() {
        return usuPass; // Le decimos a Spring cuál es la contraseña
    }

    @Override
    public String getUsername() {
        // Spring usa "username" como identificador principal.
        // Usaremos el LEGAJO convertido a String.
        return String.valueOf(usuLegajo);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}