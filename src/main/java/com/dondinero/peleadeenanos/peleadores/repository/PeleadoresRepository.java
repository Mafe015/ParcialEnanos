package com.dondinero.peleadeenanos.peleadores.repository;

import com.dondinero.peleadeenanos.peleadores.model.Peleadores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PeleadoresRepository
        extends JpaRepository<Peleadores, Long> {
    //Insertar
    //Eliminar
    //
    public Peleadores findByNombre(String nombre);
    //@Query("Consula")
}
