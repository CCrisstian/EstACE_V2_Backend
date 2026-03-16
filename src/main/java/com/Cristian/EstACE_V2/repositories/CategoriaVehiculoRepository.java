package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.CategoriaVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaVehiculoRepository extends JpaRepository<CategoriaVehiculo, Integer> {
}