package com.dondinero.peleadeenanos.peleas.repository;

import com.dondinero.peleadeenanos.peleas.models.Pelea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeleasRepository extends JpaRepository<Pelea, Long> {

    @Query("SELECT p FROM Pelea p WHERE p.peleador1.id = :id OR p.peleador2.id = :id")
    List<Pelea> findByPeleadoresId(@Param("id") Long id);
}
