package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.FotoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoProductoRepository extends JpaRepository<FotoProducto, Long> {

}