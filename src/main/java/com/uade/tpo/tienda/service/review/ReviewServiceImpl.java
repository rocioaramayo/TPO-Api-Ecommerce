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
import com.uade.tpo.tienda.exceptions.UsuarioNoAutorizadoException;
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
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productRepository.findById(request.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            boolean compro = compraRepository.existsByUsuarioAndItemsProducto(usuario, producto);
            if (!compro) {
                throw new UsuarioNoAutorizadoException();
            }

            

        Review review = Review.builder()
            .usuario(usuario)
            .producto(producto)
            .estrellas(request.getEstrellas())
            .comentario(request.getComentario())
            .fecha(LocalDateTime.now())
            .build();

        reviewRepository.save(review);
    }

    @Override
    public List<ReviewResponse> obtenerReviewsPorProducto(Long productoId) {
        Producto producto = productRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return reviewRepository.findByProducto(producto).stream().map(r ->
            ReviewResponse.builder()
                .usuario(r.getUsuario().getLoginName())
                .estrellas(r.getEstrellas())
                .comentario(r.getComentario())
                .fecha(r.getFecha().toString())
                .build()
        ).toList();
    }
}
