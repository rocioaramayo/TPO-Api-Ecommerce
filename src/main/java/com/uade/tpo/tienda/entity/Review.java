package com.uade.tpo.tienda.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Producto producto;

    @Column(nullable = false)
    private int estrellas; // de 1 a 5

    private String comentario;

    private LocalDateTime fecha;
}
