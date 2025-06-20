package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.Categoria;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Categoria, Long> {
  List<Categoria> findByDescripcion(String descripcion);
  List<Categoria> findByNombre(String nombre);
}