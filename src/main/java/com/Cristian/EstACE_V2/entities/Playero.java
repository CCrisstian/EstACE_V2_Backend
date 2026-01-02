package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playero", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Playero {

    @Id
    @Column(name = "playero_legajo")
    private Integer id;

    // Relación 1 a 1 con Usuario.
    // @MapsId indica que el ID del Playero ES el mismo ID del Usuario asociado.
    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "playero_legajo")
    private Usuario usuario;

    // Relación con Estacionamiento (Un estacionamiento tiene muchos playeros)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "est_id", nullable = false)
    private Estacionamiento estacionamiento;

    @Column(name = "playero_activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}