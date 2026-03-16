package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plaza")
@IdClass(PlazaId.class) // Indicamos que usa clave compuesta
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plaza {

    @Id
    @Column(name = "est_id")
    private Integer estId;

    @Id
    @Column(name = "plaza_id")
    private Integer plazaId;

    @Column(name = "categoria_id")
    private Integer categoriaId;

    @Column(name = "plaza_nombre", length = 50)
    private String nombre;

    @Column(name = "plaza_tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "plaza_disponibilidad", nullable = false)
    private Boolean disponibilidad;
}