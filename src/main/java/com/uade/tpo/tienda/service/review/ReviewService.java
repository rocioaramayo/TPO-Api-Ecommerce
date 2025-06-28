package com.uade.tpo.tienda.service.review;

import java.util.List;

import com.uade.tpo.tienda.dto.ReviewRequest;
import com.uade.tpo.tienda.dto.ReviewResponse;

public interface ReviewService {
    ReviewResponse dejarReview(String emailUsuario, ReviewRequest request);
    List<ReviewResponse> obtenerReviewsPorProducto(Long productoId);
}