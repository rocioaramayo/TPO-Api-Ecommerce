package com.uade.tpo.tienda.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.tienda.entity.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    // Si querés agregar métodos custom más adelante, podés hacerlo acá
}

