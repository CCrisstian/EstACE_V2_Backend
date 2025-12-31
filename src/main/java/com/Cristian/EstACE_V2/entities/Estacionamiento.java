package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "estacionamiento", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Estacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "est_id")
    private Integer id;

    // Relación con Dueño (fk: dueño_legajo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dueño_legajo", nullable = false)
    @JsonIgnore // 👈 AGREGA ESTA LÍNE
    private Dueño dueno;

    @Column(name = "est_nombre", length = 500)
    private String nombre;

    @Column(name = "est_provincia", length = 100)
    private String provincia;

    @Column(name = "est_localidad", length = 100)
    private String localidad;

    @Column(name = "est_direccion", length = 100)
    private String direccion;

    @Column(name = "est_puntaje", nullable = false)
    @Builder.Default // Para que el Builder use el valor por defecto
    private Double puntaje = 0.0;

    @Column(name = "est_dias_atencion", length = 100)
    private String diasAtencion;

    @Column(name = "est_hra_atencion", length = 50)
    private String hraAtencion;

    @Column(name = "est_dias_feriado_atencion")
    private Boolean diasFeriadoAtencion;

    @Column(name = "est_fin_de_semana_atencion")
    private Boolean finDeSemanaAtencion;

    @Column(name = "est_hora_fin_de_semana", length = 100)
    private String horaFinDeSemana;

    @Column(name = "est_disponibilidad", nullable = false)
    @Builder.Default
    private Boolean disponibilidad = true;

    @Column(name = "est_latitud")
    private Double latitud;

    @Column(name = "est_longitud")
    private Double longitud;

    @Column(name = "est_puntaje_acumulado", nullable = false)
    @Builder.Default
    private Double puntajeAcumulado = 0.0;

    @Column(name = "est_cantidad_votos", nullable = false)
    @Builder.Default
    private Integer cantidadVotos = 0;
}