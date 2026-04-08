package com.dondinero.peleadeenanos.peleadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record PeleadorCreateDTO(

        @NotBlank(message = "Ponerle nombre al peleador es obligatorio")
        @Size(min=2, max=120, message = "El nombre debe tener entre 2 y 120 caracteres")
        String nombre,

        @NotBlank(message = "La informacion es obligatoria")
        @Size(max=300, message = "La informacion no puede superar 300 caracteres")
        String informacion,

        @NotBlank(message = "La habilidad especial es obligatoria")
        @Size(max=120, message = "La habilidad especial no puede superar 120 caracteres")
        String habilidadEspecial,

        @Min(value=0, message = "La fuerza no puede ser negativa")
        @Max(value=100, message = "La fuerza máxima es 100")
        int fuerza,

        @Min(value=0, message = "La velocidad no puede ser negativa")
        @Max(value=100, message = "La velocidad máxima es 100")
        int velocidad,

        @Min(value=0, message = "La resistencia no puede ser negativa")
        @Max(value=100, message = "La resistencia máxima es 100")
        int resistencia

) {
}