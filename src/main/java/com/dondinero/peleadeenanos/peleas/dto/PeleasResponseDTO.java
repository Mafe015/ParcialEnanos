package com.dondinero.peleadeenanos.peleas.dto;

import java.math.BigDecimal;

public record PeleasResponseDTO(
    Long id,
    Long peleador1Id,
    String peleador1Nombre,
    Long peleador2Id,
    String peleador2Nombre,
    Long ganadorId,
    String ganadorNombre
) {

}
