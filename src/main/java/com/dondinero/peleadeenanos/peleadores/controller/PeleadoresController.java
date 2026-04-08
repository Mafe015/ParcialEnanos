package com.dondinero.peleadeenanos.peleadores.controller;

import com.dondinero.peleadeenanos.peleadores.dto.PeleadorCreateDTO;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorResponseDTO;
import com.dondinero.peleadeenanos.peleadores.service.PeleadoresService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Peleadores")
public class PeleadoresController {
    private final PeleadoresService peleadoresService;
    public PeleadoresController(PeleadoresService peleadoresService) {
        this.peleadoresService = peleadoresService;
    }
    @GetMapping
    public List<PeleadorResponseDTO> findAll(){
        return peleadoresService.findAll();
    }
    @GetMapping("/{id}")
    public PeleadorResponseDTO findById(@PathVariable Long id){
        return peleadoresService.findById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeleadorResponseDTO create(@Valid @RequestBody PeleadorCreateDTO dto){
        return peleadoresService.create(dto);
    }
}
