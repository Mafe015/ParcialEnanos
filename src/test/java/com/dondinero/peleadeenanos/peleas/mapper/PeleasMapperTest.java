package com.dondinero.peleadeenanos.peleas.mapper;

// ── Imports correctos ─────────────────────────────────────────────────────────
import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;    // dto real del proyecto
import com.dondinero.peleadeenanos.peleas.dto.PeleasResponseDTO;  // dto real del proyecto
import com.dondinero.peleadeenanos.peleas.models.Pelea;           // package: peleas.models
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;   // clase: Peleadores (con S)

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test UNITARIO puro del PeleasMapper.
 * No necesita Spring ni base de datos.
 * El mapper convierte entre entidades y DTOs.
 */
class PeleasMapperTest {

    // ─────────────────────────────────────────────
    // toEntity (DTO → Entidad)
    // ─────────────────────────────────────────────

    @Test
    void toEntity_mapsAllFields() {
        // Preparamos dos peleadores y un ganador
        Peleadores elTormentoso = new Peleadores();
        elTormentoso.setId(1L);
        elTormentoso.setNombre("El Tormentoso");

        Peleadores laVipera = new Peleadores();
        laVipera.setId(2L);
        laVipera.setNombre("La Vípera");

        // El ganador es el mismo Tormentoso (ya calculado por el service)
        PeleasCreateDTO dto = new PeleasCreateDTO(1L, 2L);

        // toEntity recibe: dto, peleador1, peleador2, ganador
        Pelea pelea = PeleasMapper.toEntity(dto, elTormentoso, laVipera, elTormentoso);

        // Verificamos que la pelea tiene los peleadores asignados correctamente
        assertThat(pelea.getPeleador1().getId()).isEqualTo(1L);
        assertThat(pelea.getPeleador2().getId()).isEqualTo(2L);
        assertThat(pelea.getGanador().getId()).isEqualTo(1L);
    }

    // ─────────────────────────────────────────────
    // toDto (Entidad → DTO de respuesta)
    // ─────────────────────────────────────────────

    @Test
    void toDto_withPeleadores_returnsPeleadoresFields() {
        // Construimos una pelea completa con peleadores y ganador
        Peleadores elTormentoso = new Peleadores();
        elTormentoso.setId(1L);
        elTormentoso.setNombre("El Tormentoso");

        Peleadores laVipera = new Peleadores();
        laVipera.setId(2L);
        laVipera.setNombre("La Vípera");

        Pelea pelea = new Pelea();
        pelea.setId(100L);
        pelea.setPeleador1(elTormentoso);
        pelea.setPeleador2(laVipera);
        pelea.setGanador(elTormentoso); // El Tormentoso gana

        PeleasResponseDTO dto = PeleasMapper.toDto(pelea);

        // Verificamos todos los campos del DTO de respuesta
        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.peleador1Id()).isEqualTo(1L);
        assertThat(dto.peleador1Nombre()).isEqualTo("El Tormentoso");
        assertThat(dto.peleador2Id()).isEqualTo(2L);
        assertThat(dto.peleador2Nombre()).isEqualTo("La Vípera");
        assertThat(dto.ganadorId()).isEqualTo(1L);
        assertThat(dto.ganadorNombre()).isEqualTo("El Tormentoso");
    }

    @Test
    void toDto_withNullPeleadores_returnsNullFields() {
        // Caso borde: la pelea existe pero los peleadores son null
        Pelea pelea = new Pelea();
        pelea.setId(200L);
        pelea.setPeleador1(null);
        pelea.setPeleador2(null);
        pelea.setGanador(null);

        // El mapper no debe explotar con NullPointerException
        PeleasResponseDTO dto = PeleasMapper.toDto(pelea);

        assertThat(dto.id()).isEqualTo(200L);
        assertThat(dto.peleador1Id()).isNull();
        assertThat(dto.peleador1Nombre()).isNull();
        assertThat(dto.peleador2Id()).isNull();
        assertThat(dto.peleador2Nombre()).isNull();
        assertThat(dto.ganadorId()).isNull();
        assertThat(dto.ganadorNombre()).isNull();
    }
}
