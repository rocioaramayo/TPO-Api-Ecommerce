package com.uade.tpo.tienda.entity;
 
 import jakarta.persistence.*;
 import lombok.AllArgsConstructor;
 import lombok.Builder;
 import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 @Entity
 @Table(name = "compra_item")
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 public class CompraItem {
 
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     private Integer cantidad;
     @ManyToOne
     @JoinColumn(name = "producto_id")
     private Producto producto;     
 
     @ManyToOne
     @JoinColumn(name = "compra_id")
     private Compra compra;
    

    }