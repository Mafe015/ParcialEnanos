package com.dondinero.peleadeenanos.peleas.models;

import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "peleas")
@Data
@NoArgsConstructor
public class Pelea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peleador1_id")
    private Peleadores peleador1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peleador2_id")
    private Peleadores peleador2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ganador_id")
    private Peleadores ganador;

    private LocalDateTime fecha;
}
