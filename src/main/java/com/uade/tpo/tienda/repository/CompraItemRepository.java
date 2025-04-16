package com.uade.tpo.tienda.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.tienda.entity.CompraItem;

@Repository
public interface CompraItemRepository extends JpaRepository<CompraItem, Long> {
    
}

