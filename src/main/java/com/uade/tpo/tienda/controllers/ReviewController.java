package com.uade.tpo.tienda.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.tienda.dto.ReviewRequest;
import com.uade.tpo.tienda.dto.ReviewResponse;
import com.uade.tpo.tienda.entity.Usuario;
import com.uade.tpo.tienda.service.review.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> dejarReview(@AuthenticationPrincipal Usuario usuario, @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.dejarReview(usuario.getEmail(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<List<ReviewResponse>> obtenerReviews(@PathVariable Long productoId) {
        return ResponseEntity.ok(reviewService.obtenerReviewsPorProducto(productoId));
    }
}
