package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.Plaza;
import com.Cristian.EstACE_V2.entities.PlazaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlazaRepository extends JpaRepository<Plaza, PlazaId> {
    // Buscar todas las plazas que pertenezcan a un estacionamiento
    List<Plaza> findByEstId(Integer estId);

    // Buscar plazas que pertenezcan a varios estacionamientos
    List<Plaza> findByEstIdIn(List<Integer> estIds);

    // Busca el plaza_id más alto para un estacionamiento específico
    @Query("SELECT MAX(p.plazaId) FROM Plaza p WHERE p.estId = :estId")
    Integer findMaxPlazaIdByEstId(Integer estId);

    // Busca plazas en un estacionamiento que coincidan con una lista de nombres
    List<Plaza> findByEstIdAndNombreIn(Integer estId, List<String> nombres);
}