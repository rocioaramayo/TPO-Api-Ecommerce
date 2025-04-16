package com.uade.tpo.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // Endpoints públicos
                .requestMatchers("/api/v1/auth/**", "/error/**").permitAll()

                // Gestión de usuarios - Solo ADMIN
                .requestMatchers("/usuarios/**").hasAuthority(Role.ADMIN.name())

                // Categorías
                .requestMatchers("/categories/create").hasAuthority(Role.ADMIN.name())
                .requestMatchers("/categories/**").authenticated()

                // Productos - VENDEDOR puede crear, actualizar, borrar
                .requestMatchers(HttpMethod.POST, "/productos").hasAuthority(Role.VENDEDOR.name())
                .requestMatchers(HttpMethod.PUT, "/productos/**").hasAuthority(Role.VENDEDOR.name())
                .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAuthority(Role.VENDEDOR.name())

                // Productos - COMPRADOR, VENDEDOR y ADMIN pueden consultar y filtrar
                .requestMatchers(HttpMethod.GET, "/productos/**").hasAnyAuthority(
                    Role.COMPRADOR.name(), Role.VENDEDOR.name(), Role.ADMIN.name()
                )

                // Cualquier otra petición requiere estar autenticado
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
