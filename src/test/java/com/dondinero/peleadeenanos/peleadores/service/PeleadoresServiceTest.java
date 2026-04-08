package com.dondinero.peleadeenanos.peleadores.service;

// ── Imports correctos: usamos las clases reales del proyecto ──────────────────
import com.dondinero.peleadeenanos.excepciones.NotFoundException;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorCreateDTO;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorResponseDTO;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;      
import com.dondinero.peleadeenanos.peleadores.repository.PeleadoresRepository;

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
import static org.mockito.Mockito.*;

/**
 * Test UNITARIO del PeleadoresService.
 * Usamos Mockito para simular el repositorio — no se necesita base de datos.
 * @ExtendWith(MockitoExtension.class) inicializa los mocks automáticamente.
 */
@ExtendWith(MockitoExtension.class)
class PeleadoresServiceTest {

    // @Mock crea una versión falsa del repositorio (no toca la BD real)
    @Mock
    private PeleadoresRepository peleadoresRepository;

    // @InjectMocks crea el servicio real e inyecta el mock dentro
    @InjectMocks
    private PeleadoresService peleadoresService;

    // Peleador de prueba reutilizable en todos los tests
    private Peleadores elTormentoso;

    @BeforeEach
    void setUp() {
        // Preparamos un peleador ficticio del club de Don Dinero
        elTormentoso = new Peleadores();
        elTormentoso.setId(1L);
        elTormentoso.setNombre("El Tormentoso");
        elTormentoso.setInformacion("Campeón invicto de las catacumbas de Titiribí");
        elTormentoso.setHabilidadEspecial("Golpe del Rayo Oscuro");
        elTormentoso.setFuerza(95);
        elTormentoso.setVelocidad(80);
        elTormentoso.setResistencia(90);
        elTormentoso.setPeleas(List.of()); // sin peleas aún
    }

    // ─────────────────────────────────────────────
    // create
    // ─────────────────────────────────────────────

    @Test
    void create_savesAndReturnsDTO() {
        // Armamos el DTO de entrada con los 6 campos del record
        PeleadorCreateDTO dto = new PeleadorCreateDTO(
                "El Tormentoso",
                "Campeón invicto de las catacumbas de Titiribí",
                "Golpe del Rayo Oscuro",
                95, 80, 90
        );

        // Le decimos al mock qué devolver cuando se llame save()
        when(peleadoresRepository.save(any(Peleadores.class))).thenReturn(elTormentoso);

        PeleadorResponseDTO result = peleadoresService.create(dto);

        // Verificamos los campos del DTO de respuesta (nombres exactos del record)
        assertThat(result.nombre()).isEqualTo("El Tormentoso");
        assertThat(result.habilidadEspecial()).isEqualTo("Golpe del Rayo Oscuro");
        assertThat(result.fuerza()).isEqualTo(95);
        // Verificamos que save() fue llamado exactamente una vez
        verify(peleadoresRepository, times(1)).save(any(Peleadores.class));
    }

    // ─────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────

    @Test
    void findAll_returnsListOfDTOs() {
        // Segundo peleador del club
        Peleadores laVipera = new Peleadores();
        laVipera.setId(2L);
        laVipera.setNombre("La Vípera");
        laVipera.setInformacion("Ágil como una serpiente, nunca ha perdido en menos de 10 segundos");
        laVipera.setHabilidadEspecial("Mordida Venenosa");
        laVipera.setFuerza(70);
        laVipera.setVelocidad(99);
        laVipera.setResistencia(75);
        laVipera.setPeleas(List.of());

        when(peleadoresRepository.findAll()).thenReturn(List.of(elTormentoso, laVipera));

        List<PeleadorResponseDTO> result = peleadoresService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nombre()).isEqualTo("El Tormentoso");
        assertThat(result.get(1).nombre()).isEqualTo("La Vípera");
    }

    @Test
    void findAll_returnsEmptyList_whenNoPeleadoresExist() {
        when(peleadoresRepository.findAll()).thenReturn(List.of());

        List<PeleadorResponseDTO> result = peleadoresService.findAll();

        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────
    // findById
    // ─────────────────────────────────────────────

    @Test
    void findById_returnsDTOWhenFound() {
        // Simulamos que el repo devuelve el peleador con id=1
        when(peleadoresRepository.findById(1L)).thenReturn(Optional.of(elTormentoso));

        PeleadorResponseDTO result = peleadoresService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("El Tormentoso");
    }

    @Test
    void findById_throwsNotFoundException_whenNotFound() {
        // Simulamos que el repo no encuentra nada con id=99
        when(peleadoresRepository.findById(99L)).thenReturn(Optional.empty());

        // Verificamos que el service lanza la excepción correcta con el mensaje exacto
        assertThatThrownBy(() -> peleadoresService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Peleador not found");
    }
}
