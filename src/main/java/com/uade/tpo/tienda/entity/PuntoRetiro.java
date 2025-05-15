
package com.uade.tpo.tienda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "puntos_retiro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntoRetiro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String descripcion;
    
    @Column(nullable = false)
    private String direccion;
    
    @Column(nullable = false)
    private String localidad;
    
    private String provincia;
    
    @Column(name = "codigo_postal")
    private String codigoPostal;
    
    @Column(name = "horario_atencion")
    private String horarioAtencion;
    
    private String telefono;
    
    private String email;
    
    private String coordenadas; // formato "lat,lng"
    
    @ManyToOne
    @JoinColumn(name = "metodo_entrega_id")
    private MetodoEntrega metodoEntrega;
    
    private boolean activo;
}