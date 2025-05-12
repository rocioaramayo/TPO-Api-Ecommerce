package com.uade.tpo.tienda.service.favoritos;

import com.uade.tpo.tienda.dto.FavoritoResponse;
import com.uade.tpo.tienda.dto.PhotoResponse;
import com.uade.tpo.tienda.dto.ProductResponse;
import com.uade.tpo.tienda.entity.ListaFavoritos;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.ProductoInactivoException;
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.repository.ListaFavoritosRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritosServiceImpl implements FavoritosService {

    @Autowired
    private ListaFavoritosRepository favoritosRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProductRepository productoRepository;
    
    @Override
    @Transactional
    public FavoritoResponse agregarFavorito(String emailUsuario, Long productoId) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        
        // Buscar producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException());
        
        // Verificar si el producto está activo
        if (!producto.isActivo()) {
            throw new ProductoInactivoException();
        }
        
        // Verificar si ya está en favoritos
        if (favoritosRepository.existsByUsuarioAndProducto(usuario, producto)) {
            // Si ya existe, simplemente retornamos la información
            ListaFavoritos existente = favoritosRepository.findByUsuarioAndProducto(usuario, producto)
                    .orElseThrow(); // No debería ocurrir porque ya verificamos que existe
            
            return mapToFavoritoResponse(existente);
        }
        
        // Crear nueva entrada en favoritos
        ListaFavoritos favorito = ListaFavoritos.builder()
                .usuario(usuario)
                .producto(producto)
                .fechaAgregado(LocalDateTime.now())
                .build();
        
        // Guardar y retornar
        ListaFavoritos guardado = favoritosRepository.save(favorito);
        return mapToFavoritoResponse(guardado);
    }

    @Override
    @Transactional
    public void eliminarFavorito(String emailUsuario, Long productoId) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        
        // Buscar producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException());
        
        // Buscar en favoritos
        Optional<ListaFavoritos> favoritoOpt = favoritosRepository.findByUsuarioAndProducto(usuario, producto);
        
        // Si existe, eliminar
        favoritoOpt.ifPresent(favorito -> favoritosRepository.delete(favorito));
    }

    @Override
    public List<FavoritoResponse> obtenerFavoritosUsuario(String emailUsuario) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        
        // Obtener todos sus favoritos
        List<ListaFavoritos> favoritos = favoritosRepository.findByUsuario(usuario);
        
        // Convertir a respuesta
        return favoritos.stream()
                .map(this::mapToFavoritoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean esFavorito(String emailUsuario, Long productoId) {
        // Buscar usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailUsuario);
        if (usuarioOpt.isEmpty()) {
            return false; // Si no existe el usuario, no puede tener favoritos
        }
        
        // Buscar producto
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isEmpty()) {
            return false; // Si no existe el producto, no puede estar en favoritos
        }
        
        // Verificar si está en favoritos
        return favoritosRepository.existsByUsuarioAndProducto(usuarioOpt.get(), productoOpt.get());
    }
    
    
    private FavoritoResponse mapToFavoritoResponse(ListaFavoritos favorito) {
        // Usamos el service de productos para mapear correctamente el producto
        Producto producto = favorito.getProducto();
        
        // Creamos una representación simple del producto para no exponer todos los datos
        ProductResponse productResponse = ProductResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria().getNombre())
                .fotos(mapearFotos(producto))
                .tipoCuero(producto.getTipoCuero())
                .grosor(producto.getGrosor())
                .acabado(producto.getAcabado())
                .color(producto.getColor())
                .textura(producto.getTextura())
                .instruccionesCuidado(producto.getInstruccionesCuidado())
                .createdAt(producto.getCreatedAt())
                .pocoStock(producto.getStock() < 5)
                .build();
        
        return FavoritoResponse.builder()
                .id(favorito.getId())
                .producto(productResponse)
                .fechaAgregado(favorito.getFechaAgregado())
                .build();
    }
    
    
    private List<PhotoResponse> mapearFotos(Producto producto) {
        if (producto.getFotos() == null || producto.getFotos().isEmpty()) {
            return List.of();
        }
        
        return producto.getFotos().stream()
            .map(foto -> PhotoResponse.builder()
                    .id(foto.getId())
                    .contenidoBase64(foto.getContenidoBase64())
                    .tipoContenido(foto.getTipoContenido())
                    .descripcion(foto.getDescripcion())
                    .build())
            .collect(Collectors.toList());
    }
}