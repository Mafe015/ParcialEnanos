package com.dondinero.peleadeenanos.peleadores.mapper;

import com.dondinero.peleadeenanos.peleadores.dto.PeleadorCreateDTO;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorResponseDTO;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import com.dondinero.peleadeenanos.peleas.models.Pelea;

import java.util.ArrayList;
import java.util.List;

public class PeleadoresMapper {

    public static Peleadores toPeleador(PeleadorCreateDTO dto) {
        Peleadores p = new Peleadores();
        p.setNombre(dto.nombre());
        p.setInformacion(dto.informacion());
        p.setHabilidadEspecial(dto.habilidadEspecial());
        p.setFuerza(dto.fuerza());
        p.setVelocidad(dto.velocidad());
        p.setResistencia(dto.resistencia());
        return p;
    }

    public static PeleadorResponseDTO toPeleadorResponseDTO(Peleadores p) {
        List<Long> peleasIds = p.getPeleas() == null ? new ArrayList<>() :
                p.getPeleas().stream().map(Pelea::getId).toList();
        return new PeleadorResponseDTO(
                p.getId(), p.getNombre(), p.getInformacion(),
                p.getHabilidadEspecial(), p.getFuerza(),
                p.getVelocidad(), p.getResistencia(), peleasIds
        );
    }

    public static Peleadores toPeleadoresResponse(PeleadorResponseDTO dto) {
        Peleadores p = new Peleadores();
        p.setId(dto.id());
        p.setNombre(dto.nombre());
        p.setInformacion(dto.informacion());
        p.setHabilidadEspecial(dto.habilidadEspecial());
        p.setFuerza(dto.fuerza());
        p.setVelocidad(dto.velocidad());
        p.setResistencia(dto.resistencia());
        return p;
    }
}
