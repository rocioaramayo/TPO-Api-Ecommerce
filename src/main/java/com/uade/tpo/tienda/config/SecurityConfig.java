package com.uade.tpo.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uade.tpo.tienda.enums.Role;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> req
                // Auth endpoints públicos
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/error/**").permitAll()

                // Usuarios - Solo ADMIN
                .requestMatchers("/usuarios/**").hasAuthority(Role.ADMIN.name())

                // Productos - Solo VENDEDOR puede crear, modificar y eliminar
                .requestMatchers("/productos", "/productos/", "/productos/{id}").hasAuthority(Role.VENDEDOR.name())

                // Productos - COMPRADOR y VENDEDOR pueden listar productos
                .requestMatchers("/productos", "/productos/**").hasAnyAuthority(Role.COMPRADOR.name(), Role.VENDEDOR.name())

                // Categorías - Solo ADMIN puede crear
                .requestMatchers("/categories/create").hasAuthority(Role.ADMIN.name())

                // Categorías - Todos autenticados pueden ver (si hay otros endpoints, agregar aquí)
                .requestMatchers("/categories/**").authenticated()

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
