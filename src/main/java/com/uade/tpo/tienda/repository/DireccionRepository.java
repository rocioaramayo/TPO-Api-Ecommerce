package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findAllByUsuarioEmail(String email);
}
