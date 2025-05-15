
package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.MetodoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoEntregaRepository extends JpaRepository<MetodoEntrega, Long> {
    List<MetodoEntrega> findByActivoTrue();
}