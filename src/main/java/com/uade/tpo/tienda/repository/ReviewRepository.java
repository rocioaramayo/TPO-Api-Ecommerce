package com.uade.tpo.tienda.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Review;
import com.uade.tpo.tienda.entity.Usuario;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProducto(Producto producto);
    
    boolean existsByUsuarioAndProducto(Usuario usuario, Producto producto);
}
