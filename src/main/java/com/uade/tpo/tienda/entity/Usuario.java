package com.uade.tpo.tienda.entity;
import com.uade.tpo.tienda.enums.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                    
@NoArgsConstructor        // Genera un constructor sin argumentos (requerido por JPA)
@AllArgsConstructor       // Genera un constructor con argumentos para todos los campos
@Builder                 
@Entity
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //no puede estar vacia y debe ser unica
  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @CreationTimestamp
  @Column(name= "created_at")
  private LocalDateTime createdAT;

  @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL , orphanRemoval = true)
  private Producto producto;
}
