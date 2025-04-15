package com.uade.tpo.tienda.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la compra (muchos ítems pueden pertenecer a una compra)
    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    // Relación con el producto (cada ítem hace referencia a un producto)
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;

    private Double precioUnitario; // opcional: por si querés guardar cuánto valía ese producto en ese momento
}

