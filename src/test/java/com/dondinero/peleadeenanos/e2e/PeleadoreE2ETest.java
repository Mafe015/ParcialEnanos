package com.dondinero.peleadeenanos.e2e;

// ── Imports correctos — solo clases del proyecto ──────────────────────────────
import com.dondinero.peleadeenanos.config.StubJwtDecoderConfig;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorCreateDTO;
import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test END-TO-END (E2E) — flujo completo de la aplicación.
 * Simula peticiones HTTP reales a través de todos los layers (controller → service → BD).
 * Usa H2 en memoria y el StubJwtDecoder para no necesitar Keycloak corriendo.
 * @Transactional hace rollback al final de cada test → BD limpia.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Import(StubJwtDecoderConfig.class)
@Transactional
class PeleadoresE2ETest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper; // para serializar/deserializar JSON

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Construimos MockMvc con soporte de seguridad Spring Security
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ─────────────────────────────────────────────
    // Flujo completo: crear peleador → crear pelea → leer ambos
    // ─────────────────────────────────────────────

    @Test
    void fullFlow_crearPeleadoresThenPelea_andReadBoth() throws Exception {

        // ── Paso 1: ADMIN crea al peleador "El Tormentoso" ───────────────────
        PeleadorCreateDTO tormentosoDTO = new PeleadorCreateDTO(
                "El Tormentoso",
                "Campeón invicto de las catacumbas de Titiribí",
                "Golpe del Rayo Oscuro",
                95, 80, 90
        );

        MvcResult tormentosoResult = mockMvc.perform(post("/api/peleadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tormentosoDTO)))
                .andExpect(status().isCreated())
                // Verificamos que el JSON de respuesta tiene los campos correctos
                .andExpect(jsonPath("$.nombre").value("El Tormentoso"))
                .andExpect(jsonPath("$.habilidadEspecial").value("Golpe del Rayo Oscuro"))
                .andExpect(jsonPath("$.fuerza").value(95))
                .andReturn();

        // Extraemos el ID del peleador 1 creado
        Map<?, ?> tormentosoBody = objectMapper.readValue(
                tormentosoResult.getResponse().getContentAsString(), Map.class);
        Long tormentosoId = Long.valueOf(tormentosoBody.get("id").toString());
        assertThat(tormentosoId).isPositive();

        // ── Paso 2: ADMIN crea a "La Vípera" ─────────────────────────────────
        PeleadorCreateDTO viperaDTO = new PeleadorCreateDTO(
                "La Vípera",
                "Ágil como una serpiente, terror del club de Don Dinero",
                "Mordida Venenosa",
                70, 99, 75
        );

        MvcResult viperaResult = mockMvc.perform(post("/api/peleadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(viperaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("La Vípera"))
                .andReturn();

        Map<?, ?> viperaBody = objectMapper.readValue(
                viperaResult.getResponse().getContentAsString(), Map.class);
        Long viperaId = Long.valueOf(viperaBody.get("id").toString());
        assertThat(viperaId).isPositive();

        // ── Paso 3: USER lee a El Tormentoso por ID ───────────────────────────
        mockMvc.perform(get("/api/peleadores/{id}", tormentosoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tormentosoId))
                .andExpect(jsonPath("$.nombre").value("El Tormentoso"));

        // ── Paso 4: ADMIN crea una pelea entre los dos ────────────────────────
        // PeleasCreateDTO solo necesita los IDs de los dos peleadores
        PeleasCreateDTO peleasDTO = new PeleasCreateDTO(tormentosoId, viperaId);

        MvcResult peleasResult = mockMvc.perform(post("/api/peleas")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(peleasDTO)))
                .andExpect(status().isOk()) // el controller de peleas devuelve 200 con ResponseEntity.ok()
                // La respuesta debe tener ambos peleadores y el ganador
                .andExpect(jsonPath("$.peleador1Id").value(tormentosoId))
                .andExpect(jsonPath("$.peleador2Id").value(viperaId))
                .andExpect(jsonPath("$.ganadorId").isNotEmpty()) // alguno ganó
                .andReturn();

        Map<?, ?> peleasBody = objectMapper.readValue(
                peleasResult.getResponse().getContentAsString(), Map.class);
        Long peleasId = Long.valueOf(peleasBody.get("id").toString());
        assertThat(peleasId).isPositive();
    }

    // ─────────────────────────────────────────────
    // Seguridad: sin autenticación → 401
    // ─────────────────────────────────────────────

    @Test
    void postPeleador_withoutAuthentication_returns401() throws Exception {
        // Sin JWT → el filtro de seguridad rechaza con 401
        PeleadorCreateDTO dto = new PeleadorCreateDTO(
                "Anónimo", "Sin credenciales", "Ninguna", 50, 50, 50
        );

        mockMvc.perform(post("/api/peleadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    // ─────────────────────────────────────────────
    // Seguridad: ROLE_USER no puede crear peleadores → 403
    // ─────────────────────────────────────────────

    @Test
    void postPeleador_withUserRole_returns403() throws Exception {
        // JWT con rol USER (no ADMIN) → el endpoint POST requiere ADMIN → 403 Forbidden
        PeleadorCreateDTO dto = new PeleadorCreateDTO(
                "El Intruso", "Intenta crear sin permiso", "Engaño", 40, 40, 40
        );

        mockMvc.perform(post("/api/peleadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ─────────────────────────────────────────────
    // Peleador no encontrado → 404
    // ─────────────────────────────────────────────

    @Test
    void getPeleador_notFound_returns404() throws Exception {
        // ID 99999 no existe → NotFoundException → GlobalExceptionHandler → 404
        mockMvc.perform(get("/api/peleadores/99999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
