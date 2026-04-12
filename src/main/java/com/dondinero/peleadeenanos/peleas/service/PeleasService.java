package com.dondinero.peleadeenanos.peleas.service;

import com.dondinero.peleadeenanos.excepciones.NotFoundException;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import com.dondinero.peleadeenanos.peleadores.repository.PeleadoresRepository;
import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;
import com.dondinero.peleadeenanos.peleas.models.Pelea;
import com.dondinero.peleadeenanos.peleas.repository.PeleasRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PeleasService {

    private final PeleasRepository peleasRepository;
    private final PeleadoresRepository peleadoresRepository;
    private final Random random = new Random();

    public PeleasService(PeleasRepository peleasRepository,
                         PeleadoresRepository peleadoresRepository) {
        this.peleasRepository = peleasRepository;
        this.peleadoresRepository = peleadoresRepository;
    }

    public Pelea crear(PeleasCreateDTO dto) {
        Peleadores p1 = peleadoresRepository.findById(dto.peleador1Id())
                .orElseThrow(() -> new NotFoundException("Peleador 1 no existe"));

        Peleadores p2 = peleadoresRepository.findById(dto.peleador2Id())
                .orElseThrow(() -> new NotFoundException("Peleador 2 no existe"));

        if (p1.getId().equals(p2.getId())) {
            throw new IllegalArgumentException("No pueden ser el mismo peleador");
        }

        int poder1 = calcularPoder(p1);
        int poder2 = calcularPoder(p2);

        Peleadores ganador = poder1 >= poder2 ? p1 : p2;

        Pelea pelea = new Pelea();
        pelea.setPeleador1(p1);
        pelea.setPeleador2(p2);
        pelea.setGanador(ganador);
        pelea.setFecha(LocalDateTime.now());

        return peleasRepository.save(pelea);
    }

    public void delete(Long id) {
        if (!peleasRepository.existsById(id)) {
            throw new NotFoundException("Pelea no encontrada");
        }
        peleasRepository.deleteById(id);
    }

    private int calcularPoder(Peleadores p) {
        int base = p.getFuerza() + p.getVelocidad() + p.getResistencia();
        return base + random.nextInt(20);
    }
}