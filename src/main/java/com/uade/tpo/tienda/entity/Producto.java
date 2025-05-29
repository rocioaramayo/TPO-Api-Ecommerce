package com.uade.tpo.tienda.entity;



import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private Double precio;

    @Column(nullable = false)
    private Integer stock;


    //un producto tiene muchas fotografias
    @OneToMany(mappedBy = "producto",cascade= CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FotoProducto> fotos;

    //Muchos porductos pretenecen a  una categoria
    @ManyToOne
    @JoinColumn(name="categoria_id", nullable= false)
    private Categoria categoria;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private boolean activo;

    public boolean getActivo() {
        return activo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
//atributos expecificos
    @Column(name = "tipo_cuero")
    private String tipoCuero; // "Plena flor", "Nobuck", "Anilina", etc.
    
    @Column(name = "grosor")
    private String grosor; // "Fino", "Medio", "Grueso"
    
    @Column(name = "acabado")
    private String acabado; // "Mate", "Brillante", "Vintage"
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "textura")
    private String textura; // "Liso", "Grabado", "Rugoso"
    
    // Campo para instrucciones de cuidado (segunda mejora)
    @Column(name = "instrucciones_cuidado", columnDefinition = "TEXT")
    private String instruccionesCuidado;
}
