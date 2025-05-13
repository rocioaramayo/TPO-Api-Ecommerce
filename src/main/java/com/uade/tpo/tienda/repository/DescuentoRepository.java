package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    
    Optional<Descuento> findByCodigo(String codigo);
    
    boolean existsByCodigo(String codigo);
}