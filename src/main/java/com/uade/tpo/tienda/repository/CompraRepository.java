package com.uade.tpo.tienda.repository;
 
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.uade.tpo.tienda.entity.Compra;
import com.uade.tpo.tienda.entity.Usuario;
 
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
   List<Compra> findByUsuario(Usuario usuario);
}