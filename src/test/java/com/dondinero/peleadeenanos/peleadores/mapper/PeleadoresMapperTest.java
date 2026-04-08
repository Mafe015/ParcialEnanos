package com.dondinero.peleadeenanos.peleadores.mapper;

// ── Imports correctos ─────────────────────────────────────────────────────────
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorCreateDTO;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorResponseDTO;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;   // clase principal: Peleadores 
import com.dondinero.peleadeenanos.peleas.models.Pelea;           // package: peleas.models

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test UNITARIO puro del PeleadoresMapper.
 * No necesita Spring ni base de datos — solo llama métodos estáticos del mapper.
 */
class PeleadoresMapperTest {

    // ─────────────────────────────────────────────
    // toPeleador (DTO → Entidad)
    // ─────────────────────────────────────────────

    @Test
    void toPeleador_mapsAllFieldsCorrectly() {
        // Creamos el DTO con los 6 campos del record
        PeleadorCreateDTO dto = new PeleadorCreateDTO(
                "El Demoledor",
                "Leyenda de los sótanos de Medellín, nunca ha caído en combate",
                "Cabezazo Sísmico",
                88, 65, 92
        );

        // Llamamos al método estático del mapper
        Peleadores peleador = PeleadoresMapper.toPeleador(dto);

        // Verificamos que cada campo del DTO llegó bien a la entidad
        assertThat(peleador.getNombre()).isEqualTo("El Demoledor");
        assertThat(peleador.getInformacion()).isEqualTo("Leyenda de los sótanos de Medellín, nunca ha caído en combate");
        assertThat(peleador.getHabilidadEspecial()).isEqualTo("Cabezazo Sísmico");
        assertThat(peleador.getFuerza()).isEqualTo(88);
        assertThat(peleador.getVelocidad()).isEqualTo(65);
        assertThat(peleador.getResistencia()).isEqualTo(92);
    }

    // ─────────────────────────────────────────────
    // toPeleadorResponseDTO (Entidad → DTO)
    // ─────────────────────────────────────────────

    @Test
    void toPeleadorResponseDTO_withPeleas_returnsPeleaIds() {
        // Construimos la entidad manualmente con peleas asociadas
        Peleadores peleador = new Peleadores();
        peleador.setId(1L);
        peleador.setNombre("El Terremoto");
        peleador.setInformacion("Campeón de peso mosca del club Don Dinero");
        peleador.setHabilidadEspecial("Puño de Acero");
        peleador.setFuerza(90);
        peleador.setVelocidad(75);
        peleador.setResistencia(85);

        // Creamos peleas con ID para verificar que el mapper extrae los IDs
        Pelea pelea1 = new Pelea();
        pelea1.setId(10L);
        Pelea pelea2 = new Pelea();
        pelea2.setId(20L);
        peleador.setPeleas(List.of(pelea1, pelea2));

        PeleadorResponseDTO dto = PeleadoresMapper.toPeleadorResponseDTO(peleador);

        // Verificamos todos los campos del DTO de respuesta
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nombre()).isEqualTo("El Terremoto");
        assertThat(dto.habilidadEspecial()).isEqualTo("Puño de Acero");
        assertThat(dto.fuerza()).isEqualTo(90);
        assertThat(dto.velocidad()).isEqualTo(75);
        assertThat(dto.resistencia()).isEqualTo(85);
        // El mapper debe extraer los IDs de las peleas asociadas
        assertThat(dto.peleasId()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void toPeleadorResponseDTO_withNullPeleas_returnsEmptyList() {
        // Caso borde: el peleador no tiene peleas (peleas = null)
        Peleadores peleador = new Peleadores();
        peleador.setId(2L);
        peleador.setNombre("La Sombra");
        peleador.setInformacion("Misterioso peleador sin historial conocido");
        peleador.setHabilidadEspecial("Esquiva Fantasmal");
        peleador.setFuerza(60);
        peleador.setVelocidad(98);
        peleador.setResistencia(70);
        peleador.setPeleas(null); // <-- caso null, el mapper debe retornar lista vacía

        PeleadorResponseDTO dto = PeleadoresMapper.toPeleadorResponseDTO(peleador);

        // Nunca debe lanzar NullPointerException — debe devolver lista vacía
        assertThat(dto.peleasId()).isEmpty();
    }

    // ─────────────────────────────────────────────
    // toPeleadoresResponse (ResponseDTO → Entidad)
    // ─────────────────────────────────────────────

    @Test
    void toPeleadoresResponse_mapsFieldsFromResponseDTO() {
        // Este método se usa en PeleasService para convertir el DTO de vuelta a entidad
        // El orden de los parámetros del record es: id, nombre, informacion, habilidadEspecial, fuerza, velocidad, resistencia, peleasId
        PeleadorResponseDTO dto = new PeleadorResponseDTO(
                5L,
                "El Gigante Enano",
                "El más alto de los enanos del club, apodado así irónicamente",
                "Aplastamiento Total",
                99, 50, 95,
                List.of() // sin peleas
        );

        Peleadores peleador = PeleadoresMapper.toPeleadoresResponse(dto);

        assertThat(peleador.getId()).isEqualTo(5L);
        assertThat(peleador.getNombre()).isEqualTo("El Gigante Enano");
        assertThat(peleador.getHabilidadEspecial()).isEqualTo("Aplastamiento Total");
        assertThat(peleador.getFuerza()).isEqualTo(99);
        assertThat(peleador.getVelocidad()).isEqualTo(50);
        assertThat(peleador.getResistencia()).isEqualTo(95);
    }
}
