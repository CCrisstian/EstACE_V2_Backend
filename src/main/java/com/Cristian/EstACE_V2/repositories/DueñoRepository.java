package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.Dueño;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DueñoRepository extends JpaRepository<Dueño, Integer> {
}