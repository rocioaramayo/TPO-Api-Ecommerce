// src/main/java/com/uade/tpo/tienda/service/favoritos/FavoritosService.java

package com.uade.tpo.tienda.service.favoritos;

import com.uade.tpo.tienda.dto.FavoritoResponse;
import java.util.List;

public interface FavoritosService {
    FavoritoResponse agregarFavorito(String emailUsuario, Long productoId);
    void eliminarFavorito(String emailUsuario, Long productoId);
    List<FavoritoResponse> obtenerFavoritosUsuario(String emailUsuario);
    boolean esFavorito(String emailUsuario, Long productoId);
}