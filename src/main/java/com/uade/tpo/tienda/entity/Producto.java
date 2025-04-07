package com.uade.tpo.tienda.entity;



import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table( name="productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private double precio;

    @Column(nullable = false)
    private Integer stock;

    // muchos productos pueden ser publicados por el mismo usuario
    @ManyToOne
    @JoinColumn(name="vendedor_id", nullable=false)
    private Usuario vendedor;
    //un producto tiene muchas fotografias
    @OneToMany(mappedBy = "producto",cascade= CascadeType.ALL, orphanRemoval = true)
    private List<FotoProducto> fotos;

    //Muchos porductos pretenecen a  una categoria
    @ManyToOne
    @JoinColumn(name="categoria_id", nullable= false)
    private Categoria categoria;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
