package com.dondinero.peleadeenanos.peleas.controller;

import com.dondinero.peleadeenanos.excepciones.NotFoundException;
import com.dondinero.peleadeenanos.peleas.dto.PeleasCreateDTO;
import com.dondinero.peleadeenanos.peleas.dto.PeleasResponseDTO;
import com.dondinero.peleadeenanos.peleas.mapper.PeleasMapper;
import com.dondinero.peleadeenanos.peleas.models.Pelea;
import com.dondinero.peleadeenanos.peleas.repository.PeleasRepository;
import com.dondinero.peleadeenanos.peleas.service.PeleasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/peleas")
public class PeleasController {

    private final PeleasService service;
    private final PeleasMapper mapper;
    private final PeleasRepository peleasRepository;

    public PeleasController(PeleasService service, PeleasMapper mapper, PeleasRepository peleasRepository) {
        this.service = service;
        this.mapper = mapper;
        this.peleasRepository = peleasRepository;
    }

    @GetMapping
    public List<PeleasResponseDTO> findAll() {
        return peleasRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeleasResponseDTO> findById(@PathVariable Long id) {
        Pelea pelea = peleasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pelea no encontrada"));
        return ResponseEntity.ok(mapper.toDTO(pelea));
    }

    @PostMapping
    public ResponseEntity<PeleasResponseDTO> crear(@Valid @RequestBody PeleasCreateDTO dto) {
        Pelea pelea = service.crear(dto);
        return ResponseEntity.ok(mapper.toDTO(pelea));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}