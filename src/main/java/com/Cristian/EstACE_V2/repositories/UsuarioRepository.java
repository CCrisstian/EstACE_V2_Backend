package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuLegajo(Integer usuLegajo);
}