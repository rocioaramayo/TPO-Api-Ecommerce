package com.uade.tpo.tienda.entity;
import  com.uade.tpo.tienda.enums.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;


@Data
@Entity
@Builder // para creaccion distinta

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(nullable = false, unique = true)// no deja q estre vacio y se sea unico
    private String username;

    @Column(nullable = false, unique = true)// no deja q estre vacio y se sea unico
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
    @Column(name = "created_at")
    private LocalTime createdAT;
}
