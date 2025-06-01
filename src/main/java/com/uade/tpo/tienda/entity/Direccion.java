package com.uade.tpo.tienda.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String calle;
    private String numero;
    private String piso;
    private String departamento;

    private String localidad;
    private String provincia;
    private String codigoPostal;

    private String telefonoContacto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // cada dirección pertenece a un usuario
}


