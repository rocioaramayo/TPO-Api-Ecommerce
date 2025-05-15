
package com.uade.tpo.tienda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metodos_entrega")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoEntrega {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre; // "Envío a domicilio", "Punto de retiro", etc.
    
    private String descripcion;
    
    @Column(nullable = false)
    private Double costoBase;
    
    @Column(nullable = false)
    private boolean activo;
    
    @Column(name = "tiempo_estimado_dias")
    private Integer tiempoEstimadoDias;
    
    @Column(name = "requiere_direccion")
    private boolean requiereDireccion;
    
    @Column(name = "requiere_punto_retiro")
    private boolean requierePuntoRetiro;
}