package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categoria_vehiculo")
@Data
public class CategoriaVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Integer id;

    @Column(name = "categoria_descripcion", length = 1000)
    private String descripcion;
}