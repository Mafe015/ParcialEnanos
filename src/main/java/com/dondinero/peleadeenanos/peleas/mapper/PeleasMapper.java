package com.dondinero.peleadeenanos.peleas.mapper;

import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;
import com.dondinero.peleadeenanos.peleas.dto.PeleasResponseDTO;
import com.dondinero.peleadeenanos.peleas.models.Pelea;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PeleasMapper {

    public static Pelea toEntity(PeleasCreateDTO dto, Peleadores p1, Peleadores p2, Peleadores ganador) {
        Pelea pelea = new Pelea();
        pelea.setPeleador1(p1);
        pelea.setPeleador2(p2);
        pelea.setGanador(ganador);
        pelea.setFecha(LocalDateTime.now());
        return pelea;
    }

    public static PeleasResponseDTO toDto(Pelea pelea) {
        return new PeleasResponseDTO(
            pelea.getId(),
            pelea.getPeleador1() != null ? pelea.getPeleador1().getId() : null,
            pelea.getPeleador1() != null ? pelea.getPeleador1().getNombre() : null,
            pelea.getPeleador2() != null ? pelea.getPeleador2().getId() : null,
            pelea.getPeleador2() != null ? pelea.getPeleador2().getNombre() : null,
            pelea.getGanador() != null ? pelea.getGanador().getId() : null,
            pelea.getGanador() != null ? pelea.getGanador().getNombre() : null
        );
    }

    // Alias para compatibilidad con el controller que llama mapper.toDTO(pelea)
    public PeleasResponseDTO toDTO(Pelea pelea) {
        return toDto(pelea);
    }
}
