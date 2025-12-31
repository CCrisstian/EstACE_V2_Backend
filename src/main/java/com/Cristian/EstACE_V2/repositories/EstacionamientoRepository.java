package com.Cristian.EstACE_V2.repositories;

import com.Cristian.EstACE_V2.entities.Estacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstacionamientoRepository extends JpaRepository<Estacionamiento, Integer> {

    // Buscar todos los estacionamientos de un dueño específico
    List<Estacionamiento> findByDuenoId(Integer duenoId);

    // Validar duplicados por Dirección
    boolean existsByProvinciaAndLocalidadAndDireccion(String provincia, String localidad, String direccion);

    // Validar duplicados por Coordenadas
    boolean existsByLatitudAndLongitud(Double latitud, Double longitud);

    // Método personalizado para validar duplicados excluyendo el propio ID (para Edición)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Estacionamiento e " +
            "WHERE e.id <> :id AND (" +
            "(e.provincia = :provincia AND e.localidad = :localidad AND e.direccion = :direccion) OR " +
            "(e.latitud = :latitud AND e.longitud = :longitud))")
    boolean existsDuplicadoParaEditar(Integer id, String provincia, String localidad, String direccion, Double latitud, Double longitud);
}