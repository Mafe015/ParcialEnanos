package com.dondinero.peleadeenanos.peleadores.model;

import com.dondinero.peleadeenanos.peleas.models.Pelea;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "peleadores")
@Data
@NoArgsConstructor
public class Peleadores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 300)
    private String informacion;

    @Column(nullable = false, length = 120)
    private String habilidadEspecial;

    @Column(nullable = false)
    private int fuerza;

    @Column(nullable = false)
    private int velocidad;

    @Column(nullable = false)
    private int resistencia;

    @OneToMany(mappedBy = "peleador1")
    private List<Pelea> peleas;
}
