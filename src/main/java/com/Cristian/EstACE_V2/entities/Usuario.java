package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_legajo")
    private Integer usuLegajo;

    @Column(name = "usu_dni", unique = true)
    private Integer usuDni;

    @Column(name = "usu_pass", length = 12)
    private String usuPass;

    @Column(name = "usu_ap", length = 25)
    private String usuAp;

    @Column(name = "usu_nom", length = 25)
    private String usuNom;

    @Column(name = "usu_tipo", length = 50)
    private String usuTipo;
}