package com.dondinero.peleadeenanos.peleas.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PeleasCreateDTO(
        @NotNull(message = "El id del primer peleador es obligatorio")
        @Positive(message = "El id debe ser positivo")
        Long peleador1Id,

        @NotNull(message = "El id del segundo peleador es obligatorio")
        @Positive(message = "El id debe ser positivo")
        Long peleador2Id
        
) {
}
