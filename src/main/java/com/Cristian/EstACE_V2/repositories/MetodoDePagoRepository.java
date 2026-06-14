package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.MetodoDePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoDePagoRepository extends JpaRepository<MetodoDePago, Integer> {
}