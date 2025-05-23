package com.uade.tpo.tienda.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fotos_producto")

public class FotoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonBackReference
    private Producto producto;

    // Almacenar el contenido Base64 de la imagen
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contenidoBase64;
    
    // Guardar el tipo de contenido (MIME type)
    @Column(nullable = false)
    private String tipoContenido; // e.g., "image/jpeg", "image/png"
    
    private String descripcion;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
