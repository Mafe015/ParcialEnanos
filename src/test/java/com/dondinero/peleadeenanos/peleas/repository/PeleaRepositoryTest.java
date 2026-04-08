package com.dondinero.peleadeenanos.peleas.repository;

import com.dondinero.peleadeenanos.config.StubJwtDecoderConfig;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import com.dondinero.peleadeenanos.peleadores.repository.PeleadoresRepository;
import com.dondinero.peleadeenanos.peleas.models.Pelea;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de INTEGRACIÓN del PeleasRepository.
 * Usa H2 en memoria (perfil "test") y hace rollback tras cada test.
 * Necesitamos también PeleadoresRepository porque Pelea depende de Peleadores.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(StubJwtDecoderConfig.class)
@Transactional
class PeleaRepositoryTest {

    // Repositorio que estamos probando
    @Autowired
    private PeleasRepository peleasRepository;

    // Necesario para crear peleadores que se asocien a las peleas
    @Autowired
    private PeleadoresRepository peleadoresRepository;

    // Peleadores base reutilizables
    private Peleadores elTormentoso;
    private Peleadores laVipera;

    @BeforeEach
    void setUp() {
        // Creamos y persistimos dos peleadores del club de Don Dinero
        Peleadores p1 = new Peleadores();
        p1.setNombre("El Tormentoso");
        p1.setInformacion("Campeón invicto de las catacumbas de Titiribí");
        p1.setHabilidadEspecial("Golpe del Rayo Oscuro");
        p1.setFuerza(95);
        p1.setVelocidad(80);
        p1.setResistencia(90);
        elTormentoso = peleadoresRepository.save(p1);

        Peleadores p2 = new Peleadores();
        p2.setNombre("La Vípera");
        p2.setInformacion("Ágil como una serpiente, terror del club");
        p2.setHabilidadEspecial("Mordida Venenosa");
        p2.setFuerza(70);
        p2.setVelocidad(99);
        p2.setResistencia(75);
        laVipera = peleadoresRepository.save(p2);
    }

    // ─────────────────────────────────────────────
    // save
    // ─────────────────────────────────────────────

    @Test
    void save_persistsPelea() {
        Pelea pelea = new Pelea();
        pelea.setPeleador1(elTormentoso);
        pelea.setPeleador2(laVipera);
        pelea.setGanador(elTormentoso); // El Tormentoso gana

        Pelea saved = peleasRepository.save(pelea);

        // JPA debe asignar un ID y mantener las relaciones
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPeleador1().getId()).isEqualTo(elTormentoso.getId());
        assertThat(saved.getPeleador2().getId()).isEqualTo(laVipera.getId());
        assertThat(saved.getGanador().getId()).isEqualTo(elTormentoso.getId());
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_includesPersistedPeleas() {
        // Guardamos dos peleas
        Pelea p1 = new Pelea();
        p1.setPeleador1(elTormentoso);
        p1.setPeleador2(laVipera);
        p1.setGanador(elTormentoso);

        Pelea p2 = new Pelea();
        p2.setPeleador1(laVipera);
        p2.setPeleador2(elTormentoso);
        p2.setGanador(laVipera);

        peleasRepository.save(p1);
        peleasRepository.save(p2);

        List<Pelea> result = peleasRepository.findAll();

        // Deben aparecer al menos las dos que acabamos de guardar
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    // ─────────────────────────────────────────────
    // findByPeleadoresId (query personalizada)
    // ─────────────────────────────────────────────

    @Test
    void findByPeleadoresId_returnsOnlyMatchingPeleas() {
        // Pelea donde El Tormentoso participa
        Pelea pelea1 = new Pelea();
        pelea1.setPeleador1(elTormentoso);
        pelea1.setPeleador2(laVipera);
        pelea1.setGanador(elTormentoso);
        pelea1 = peleasRepository.save(pelea1);

        // Pelea donde El Tormentoso NO participa
        Pelea pelea2 = new Pelea();
        pelea2.setPeleador1(laVipera);
        pelea2.setPeleador2(laVipera); // mismos (para simplificar)
        pelea2.setGanador(laVipera);
        peleasRepository.save(pelea2);

        // Buscamos peleas del Tormentoso — debe traer solo pelea1
        List<Pelea> result = peleasRepository.findByPeleadoresId(elTormentoso.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pelea1.getId());
    }

    @Test
    void findByPeleadoresId_returnsEmpty_whenPeleadorHasNoPeleas() {
        // El Demoledor es nuevo y nunca ha peleado
        Peleadores elDemoledor = new Peleadores();
        elDemoledor.setNombre("El Demoledor");
        elDemoledor.setInformacion("Recién ingresado al club");
        elDemoledor.setHabilidadEspecial("Cabezazo Sísmico");
        elDemoledor.setFuerza(88);
        elDemoledor.setVelocidad(65);
        elDemoledor.setResistencia(92);
        elDemoledor = peleadoresRepository.save(elDemoledor);

        List<Pelea> result = peleasRepository.findByPeleadoresId(elDemoledor.getId());

        // Sin peleas → lista vacía
        assertThat(result).isEmpty();
    }
}
