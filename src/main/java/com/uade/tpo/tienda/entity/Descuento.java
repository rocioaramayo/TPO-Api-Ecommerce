package com.uade.tpo.tienda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "descuentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descuento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private Double porcentaje; // Valor entre 0 y 100
    
    private String descripcion;
    
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    
    @Column(nullable = false)
    private boolean activo;
    
    @Column(name = "monto_minimo")
    private Double montoMinimo; // Monto mínimo para aplicar el descuento
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoriaAplicable; // Si el descuento aplica a una categoría específica
    
    // Método para verificar si el descuento está vigente
    @Transient
    public boolean isVigente() {
        LocalDateTime now = LocalDateTime.now();
        return activo &&
               (fechaInicio == null || now.isAfter(fechaInicio)) &&
               (fechaFin == null || now.isBefore(fechaFin));
    }
}
