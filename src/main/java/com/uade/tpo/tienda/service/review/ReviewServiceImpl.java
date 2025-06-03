package com.uade.tpo.tienda.service.review;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.tienda.dto.ReviewRequest;
import com.uade.tpo.tienda.dto.ReviewResponse;
import com.uade.tpo.tienda.entity.Producto;
import com.uade.tpo.tienda.entity.Review;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.exceptions.ProductoNoEncontradoException;
import com.uade.tpo.tienda.exceptions.UsuarioNoAutorizadoException;
import com.uade.tpo.tienda.exceptions.UsuarioNoEncontradoException;
import com.uade.tpo.tienda.exceptions.ReviewYaExisteException;
import com.uade.tpo.tienda.repository.CompraRepository;
import com.uade.tpo.tienda.repository.ProductRepository;
import com.uade.tpo.tienda.repository.ReviewRepository;
import com.uade.tpo.tienda.repository.UsuarioRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Override
    public void dejarReview(String email, ReviewRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsuarioNoEncontradoException());

        Producto producto = productRepository.findById(request.getProductoId())
            .orElseThrow(() -> new ProductoNoEncontradoException());

        // Verificar si el usuario compró el producto
        boolean compro = compraRepository.existsByUsuarioAndItemsProducto(usuario, producto);
        if (!compro) {
            throw new UsuarioNoAutorizadoException();
        }

        // Verificar si ya existe una review del usuario para este producto
        boolean yaExisteReview = reviewRepository.existsByUsuarioAndProducto(usuario, producto);
        if (yaExisteReview) {
            throw new ReviewYaExisteException();
        }

        // Procesar el título: si está vacío o solo espacios, guardar como null
        String titulo = request.getTitulo();
        if (titulo != null && titulo.trim().isEmpty()) {
            titulo = null;
        }

        Review review = Review.builder()
            .usuario(usuario)
            .producto(producto)
            .estrellas(request.getEstrellas())
            .titulo(titulo) // Guardar el título procesado
            .comentario(request.getComentario())
            .fecha(LocalDateTime.now())
            .build();

        reviewRepository.save(review);
    }

    @Override
    public List<ReviewResponse> obtenerReviewsPorProducto(Long productoId) {
        Producto producto = productRepository.findById(productoId)
            .orElseThrow(() -> new ProductoNoEncontradoException());

        return reviewRepository.findByProducto(producto).stream().map(r ->
            ReviewResponse.builder()
                .id(r.getId())
                .nombreUsuario(r.getUsuario().getLoginName())
                .rating(r.getEstrellas())  // Mapear estrellas -> rating
                .titulo(r.getTitulo())     // Incluir el título
                .comentario(r.getComentario())
                .fecha(r.getFecha().toString())
                .build()
        ).toList();
    }
}