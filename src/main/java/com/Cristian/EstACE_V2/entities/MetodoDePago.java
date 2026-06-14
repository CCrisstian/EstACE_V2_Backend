package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metodos_de_pago", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MetodoDePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_pago_id")
    private Integer id;

    @Column(name = "metodo_pago_descripcion", length = 1000)
    private String descripcion;
}