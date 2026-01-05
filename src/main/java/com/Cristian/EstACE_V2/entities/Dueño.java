package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dueño", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Dueño {

    @Id
    @Column(name = "dueño_legajo")
    private Integer id;

    // Relación 1 a 1 con Usuario: El dueño "ES" un usuario.
    // @MapsId hace que el ID de Dueño sea el mismo que el de Usuario.
    @OneToOne
    @MapsId
    @JoinColumn(name = "dueño_legajo")
    private Usuario usuario;

}