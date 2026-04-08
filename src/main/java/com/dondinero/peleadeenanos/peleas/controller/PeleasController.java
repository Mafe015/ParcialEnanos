package com.dondinero.peleadeenanos.peleas.controller;

import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;
import com.dondinero.peleadeenanos.peleas.dto.PeleasResponseDTO;
import com.dondinero.peleadeenanos.peleas.mapper.PeleasMapper;
import com.dondinero.peleadeenanos.peleas.models.Pelea;
import com.dondinero.peleadeenanos.peleas.service.PeleasService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/peleas")
public class PeleasController {

    private final PeleasService service;
    private final PeleasMapper mapper;

    public PeleasController(PeleasService service, PeleasMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PeleasResponseDTO> crear(@Valid @RequestBody PeleasCreateDTO dto) {
        Pelea pelea = service.crear(dto);
        return ResponseEntity.ok(mapper.toDTO(pelea));
    }
}
