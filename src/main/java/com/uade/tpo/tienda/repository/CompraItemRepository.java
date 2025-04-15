package com.uade.tpo.tienda.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.tienda.entity.CompraItem;

public interface CompraItemRepository extends JpaRepository<CompraItem, Long> {
    // Podés agregar métodos personalizados si los necesitás, por ahora con esto alcanza.
}

