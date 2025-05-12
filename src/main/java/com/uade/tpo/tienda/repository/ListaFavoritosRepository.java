package com.uade.tpo.tienda.repository;

import com.uade.tpo.tienda.entity.ListaFavoritos;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaFavoritosRepository extends JpaRepository<ListaFavoritos, Long> {
    
    // Buscar todos los favoritos de un usuario
    List<ListaFavoritos> findByUsuario(Usuario usuario);
    
    // Verificar si un producto está en favoritos
    boolean existsByUsuarioAndProducto(Usuario usuario, Producto producto);
    
    // Buscar entrada específica para eliminar
    Optional<ListaFavoritos> findByUsuarioAndProducto(Usuario usuario, Producto producto);
}