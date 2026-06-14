package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.AceptaMetodoDePago;
import com.Cristian.EstACE_V2.entities.AceptaMetodoDePagoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AceptaMetodoDePagoRepository extends JpaRepository<AceptaMetodoDePago, AceptaMetodoDePagoId> {

    // Busca los métodos aceptados para un solo estacionamiento
    List<AceptaMetodoDePago> findByEstacionamientoId(Integer estId);

    // Busca los métodos aceptados para una lista de estacionamientos
    List<AceptaMetodoDePago> findByEstacionamientoIdIn(List<Integer> estIds);
}