package com.dondinero.peleadeenanos.peleadores.dto;

import java.util.List;

public record PeleadorResponseDTO(
    Long id,
    String nombre,
    String informacion,
    String habilidadEspecial,
    int fuerza,
    int velocidad,
    int resistencia,
    List<Long> peleasId   // los tests esperan este campo
) {}