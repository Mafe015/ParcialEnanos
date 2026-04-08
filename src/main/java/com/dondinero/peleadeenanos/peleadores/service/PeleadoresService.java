package com.dondinero.peleadeenanos.peleadores.service;

import com.dondinero.peleadeenanos.peleadores.dto.PeleadorCreateDTO;
import com.dondinero.peleadeenanos.peleadores.dto.PeleadorResponseDTO;
import com.dondinero.peleadeenanos.peleadores.mapper.PeleadoresMapper;
import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import com.dondinero.peleadeenanos.peleadores.repository.PeleadoresRepository;
import com.dondinero.peleadeenanos.excepciones.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeleadoresService {

    private final PeleadoresRepository peleadoresRepository;
    /*@Autowired
    private PeleadoresRepository pRepo;
    */

    public PeleadoresService(PeleadoresRepository peleadoresRepository) {
        this.peleadoresRepository = peleadoresRepository;
    }
    public PeleadorResponseDTO create(PeleadorCreateDTO dto){
        Peleadores peleador = PeleadoresMapper.toPeleador(dto);
        Peleadores saved=peleadoresRepository.save(peleador);
        return PeleadoresMapper.toPeleadorResponseDTO(saved);
    }
    public List<PeleadorResponseDTO> findAll(){
        return peleadoresRepository.findAll().stream()
                .map(PeleadoresMapper::toPeleadorResponseDTO)
                .toList();
    }
    public PeleadorResponseDTO findById(Long id){
        Peleadores peleador=peleadoresRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Peleador not found"));
        return PeleadoresMapper.toPeleadorResponseDTO(peleador);
    }

}
