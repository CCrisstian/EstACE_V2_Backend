package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.Playero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayeroRepository extends JpaRepository<Playero, Integer> {

    // Buscar playeros asociados a un dueño específico (a través del estacionamiento)
    @Query("SELECT p FROM Playero p WHERE p.estacionamiento.dueno.id = :dueñoLegajo")
    List<Playero> findByDueñoLegajo(Integer dueñoLegajo);
}