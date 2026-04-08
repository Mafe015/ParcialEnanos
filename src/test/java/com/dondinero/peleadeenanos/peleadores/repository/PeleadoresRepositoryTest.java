package com.dondinero.peleadeenanos.peleadores.repository;

import com.dondinero.peleadeenanos.config.StubJwtDecoderConfig;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de INTEGRACIÓN del PeleadoresRepository.
 * Levanta el contexto real de Spring con base de datos H2 en memoria (perfil "test").
 * @Transactional hace rollback automático al final de cada test → BD siempre limpia.
 * Spring Boot 4 eliminó @DataJpaTest, por eso usamos @SpringBootTest completo.
 */
@SpringBootTest
@ActiveProfiles("test")         // usa application-test.yml con H2
@Import(StubJwtDecoderConfig.class)  // reemplaza el JwtDecoder de Keycloak con uno falso
@Transactional                  // cada test hace rollback → no quedan datos sucios
class PeleadoresRepositoryTest {

    // Spring inyecta el repositorio real (conectado a H2)
    @Autowired
    private PeleadoresRepository peleadoresRepository;

    // Peleador base para reutilizar en todos los tests
    private Peleadores elTormentoso;

    @BeforeEach
    void setUp() {
        // "El Tormentoso" — reclutado por Don Dinero en las montañas de Titiribí
        elTormentoso = new Peleadores();
        elTormentoso.setNombre("El Tormentoso");
        elTormentoso.setInformacion("Campeón invicto de las catacumbas de Titiribí");
        elTormentoso.setHabilidadEspecial("Golpe del Rayo Oscuro");
        elTormentoso.setFuerza(95);
        elTormentoso.setVelocidad(80);
        elTormentoso.setResistencia(90);
        // Nota: NO llamamos save() aquí — cada test decide si guardar o no
    }

    // ─────────────────────────────────────────────
    // save + findById
    // ─────────────────────────────────────────────

    @Test
    void save_persistsPeleador() {
        Peleadores saved = peleadoresRepository.save(elTormentoso);

        // Después de guardar, JPA debe asignar un ID generado automáticamente
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("El Tormentoso");
    }

    @Test
    void findById_returnsPeleador_afterSave() {
        Peleadores saved = peleadoresRepository.save(elTormentoso);

        // Buscamos por el ID que JPA asignó
        Optional<Peleadores> found = peleadoresRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("El Tormentoso");
    }

    @Test
    void findById_returnsEmpty_whenNotExist() {
        // Un ID que nunca existió debe devolver Optional vacío (no excepción)
        Optional<Peleadores> found = peleadoresRepository.findById(9999L);

        assertThat(found).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_includesPersistedPeleador() {
        peleadoresRepository.save(elTormentoso);

        List<Peleadores> all = peleadoresRepository.findAll();

        // La lista debe tener al menos el peleador que acabamos de guardar
        assertThat(all).isNotEmpty();
        assertThat(all).anyMatch(p -> p.getNombre().equals("El Tormentoso"));
    }

    // ─────────────────────────────────────────────
    // findByNombre (método custom del repositorio)
    // ─────────────────────────────────────────────

    @Test
    void findByNombre_returnsCorrectPeleador() {
        peleadoresRepository.save(elTormentoso);

        // Spring Data genera la query automáticamente a partir del nombre del método
        Peleadores found = peleadoresRepository.findByNombre("El Tormentoso");

        assertThat(found).isNotNull();
        assertThat(found.getHabilidadEspecial()).isEqualTo("Golpe del Rayo Oscuro");
    }

    @Test
    void findByNombre_returnsNull_whenNotFound() {
        // Si no existe, findByNombre devuelve null (no Optional, no excepción)
        Peleadores found = peleadoresRepository.findByNombre("Peleador Inexistente xyz987");

        assertThat(found).isNull();
    }
}
