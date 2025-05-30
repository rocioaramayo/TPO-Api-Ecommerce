package com.uade.tpo.tienda.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metodo_entrega_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoEntregaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private MetodoEntrega metodoEntrega;
}
