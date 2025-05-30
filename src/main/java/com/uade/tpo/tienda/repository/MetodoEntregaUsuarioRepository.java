package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.MetodoEntregaUsuario;
import com.uade.tpo.tienda.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetodoEntregaUsuarioRepository extends JpaRepository<MetodoEntregaUsuario, Long> {
    List<MetodoEntregaUsuario> findByUsuario(Usuario usuario);
}
