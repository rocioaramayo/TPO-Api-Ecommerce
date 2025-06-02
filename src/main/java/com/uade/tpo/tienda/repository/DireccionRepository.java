package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findAllByUsuarioEmail(String email);
    List<Direccion> findByUsuarioId(Long usuarioId);
}