package com.uade.tpo.tienda.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.tienda.entity.Producto;


@Repository
public interface ProductRepository extends JpaRepository <Producto, Long> {
  
}
