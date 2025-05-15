
package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.MetodoEntrega;
import com.uade.tpo.tienda.entity.PuntoRetiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuntoRetiroRepository extends JpaRepository<PuntoRetiro, Long> {
    List<PuntoRetiro> findByActivoTrue();
    List<PuntoRetiro> findByMetodoEntregaAndActivoTrue(MetodoEntrega metodoEntrega);
}