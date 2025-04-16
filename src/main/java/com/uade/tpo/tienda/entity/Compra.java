package com.uade.tpo.tienda.entity;
 
 import jakarta.persistence.*;
 import lombok.*;
 
 import java.time.LocalDateTime;
 import java.util.List;
 
 @Entity
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 public class Compra {
 
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     private LocalDateTime fecha;
 
     private Double total;
 
     // Relación con los ítems de la compra
     @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
     private List<CompraItem> items;
 
     // Relación con el usuario que hizo la compra
     @ManyToOne
     @JoinColumn(name = "usuario_id")
     private Usuario usuario;
    }