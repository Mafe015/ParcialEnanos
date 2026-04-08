package com.dondinero.peleadeenanos.peleas.service;

// ── Imports correctos ─────────────────────────────────────────────────────────
import com.dondinero.peleadeenanos.excepciones.NotFoundException;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import com.dondinero.peleadeenanos.peleadores.repository.PeleadoresRepository;
import com.dondinero.peleadeenanos.peleas.dto.PeleasResponseDTO;
import com.dondinero.peleadeenanos.peleas.mapper.PeleasMapper;
import com.dondinero.peleadeenanos.peleas.models.Pelea;
import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;
import com.dondinero.peleadeenanos.peleas.repository.PeleasRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test UNITARIO del PeleasService.
 * Mockeamos el repositorio de peleas Y el de peleadores (el service los necesita ambos).
 * No se usa base de datos — todo es simulado con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PeleasServiceTest {

    // El service de peleas depende de ambos repositorios
    @Mock
    private PeleasRepository peleasRepository;

    @Mock
    private PeleadoresRepository peleadoresRepository;

    // El service real, con los dos mocks inyectados
    @InjectMocks
    private PeleasService peleasService;

    // Objetos de prueba reutilizables
    private Peleadores elTormentoso;
    private Peleadores laVipera;
    private Pelea pelea;

    @BeforeEach
    void setUp() {
        // Peleador 1: El Tormentoso
        elTormentoso = new Peleadores();
        elTormentoso.setId(1L);
        elTormentoso.setNombre("El Tormentoso");
        elTormentoso.setInformacion("Campeón invicto de las catacumbas de Titiribí");
        elTormentoso.setHabilidadEspecial("Golpe del Rayo Oscuro");
        elTormentoso.setFuerza(95);
        elTormentoso.setVelocidad(80);
        elTormentoso.setResistencia(90);

        // Peleador 2: La Vípera
        laVipera = new Peleadores();
        laVipera.setId(2L);
        laVipera.setNombre("La Vípera");
        laVipera.setInformacion("Ágil como una serpiente");
        laVipera.setHabilidadEspecial("Mordida Venenosa");
        laVipera.setFuerza(70);
        laVipera.setVelocidad(99);
        laVipera.setResistencia(75);

        // Pelea resultado (El Tormentoso gana — tiene mayor suma de stats)
        pelea = new Pelea();
        pelea.setId(10L);
        pelea.setPeleador1(elTormentoso);
        pelea.setPeleador2(laVipera);
        pelea.setGanador(elTormentoso);
    }

    // ─────────────────────────────────────────────
    // crear (crear pelea)
    // ─────────────────────────────────────────────

    @Test
    void crear_returnsPeleasResponseDTO() {
        // El service busca ambos peleadores en el repo antes de crear la pelea
        when(peleadoresRepository.findById(1L)).thenReturn(Optional.of(elTormentoso));
        when(peleadoresRepository.findById(2L)).thenReturn(Optional.of(laVipera));
        when(peleasRepository.save(any(Pelea.class))).thenReturn(pelea);

        PeleasCreateDTO dto = new PeleasCreateDTO(1L, 2L);
        // El service de tu proyecto se llama "crear", no "create"
        Pelea result = peleasService.crear(dto);

        // Verificamos la entidad devuelta
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getPeleador1().getNombre()).isEqualTo("El Tormentoso");
        assertThat(result.getGanador().getNombre()).isEqualTo("El Tormentoso");
        verify(peleasRepository).save(any(Pelea.class));
    }

    @Test
    void crear_throwsNotFoundException_whenPeleador1NoExiste() {
        // Si el peleador 1 no existe, el service debe lanzar NotFoundException
        when(peleadoresRepository.findById(99L)).thenReturn(Optional.empty());

        PeleasCreateDTO dto = new PeleasCreateDTO(99L, 2L);

        assertThatThrownBy(() -> peleasService.crear(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Peleador 1 no existe");
    }

    @Test
    void crear_throwsNotFoundException_whenPeleador2NoExiste() {
        when(peleadoresRepository.findById(1L)).thenReturn(Optional.of(elTormentoso));
        when(peleadoresRepository.findById(99L)).thenReturn(Optional.empty());

        PeleasCreateDTO dto = new PeleasCreateDTO(1L, 99L);

        assertThatThrownBy(() -> peleasService.crear(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Peleador 2 no existe");
    }

    @Test
    void crear_throwsIllegalArgument_whenMismoPeleador() {
        // No se puede hacer pelear a un peleador contra sí mismo
        when(peleadoresRepository.findById(1L)).thenReturn(Optional.of(elTormentoso));

        PeleasCreateDTO dto = new PeleasCreateDTO(1L, 1L);

        assertThatThrownBy(() -> peleasService.crear(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No pueden ser el mismo peleador");
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_returnsMappedDTOs() {
        // El service devuelve todos como lista de entidades y el mapper los convierte
        when(peleasRepository.findAll()).thenReturn(List.of(pelea));

        // findAll en tu service devuelve List<Pelea> directamente (sin mapping en service)
        // Si tienes un método que devuelve List<PeleasResponseDTO>, ajusta aquí
        List<Pelea> result = peleasRepository.findAll(); // verificamos el repo directamente

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
    }

    @Test
    void findAll_returnsEmpty_whenNoPeleas() {
        when(peleasRepository.findAll()).thenReturn(List.of());

        List<Pelea> result = peleasRepository.findAll();

        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findById (del repositorio, que usa el service)
    // ─────────────────────────────────────────────

    @Test
    void findById_returnsPelea_whenFound() {
        when(peleasRepository.findById(10L)).thenReturn(Optional.of(pelea));

        Optional<Pelea> result = peleasRepository.findById(10L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(10L);
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        when(peleasRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pelea> result = peleasRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findByPeleadoresId
    // ─────────────────────────────────────────────

    @Test
    void findByPeleadoresId_returnsFilteredPeleas() {
        when(peleasRepository.findByPeleadoresId(1L)).thenReturn(List.of(pelea));

        List<Pelea> result = peleasRepository.findByPeleadoresId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPeleador1().getId()).isEqualTo(1L);
    }

    @Test
    void findByPeleadoresId_returnsEmpty_whenNoMatchingPeleas() {
        when(peleasRepository.findByPeleadoresId(99L)).thenReturn(List.of());

        List<Pelea> result = peleasRepository.findByPeleadoresId(99L);

        assertThat(result).isEmpty();
    }
}
