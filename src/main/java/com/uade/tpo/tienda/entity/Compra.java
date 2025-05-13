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

    private Double subtotal; // Nuevo campo para guardar el subtotal sin descuento
    
    @Column(name = "codigo_descuento")
    private String codigoDescuento;
    
    @Column(name = "porcentaje_descuento")
    private Double porcentajeDescuento;
    
    @Column(name = "monto_descuento")
    private Double montoDescuento;
    
    // El campo 'total' tiene el total con descuento aplicado
    }