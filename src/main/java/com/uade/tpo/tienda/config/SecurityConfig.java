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
            // Desactiva CSRF porque usamos JWT (stateless)
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(req -> req

                // endpoints públicos: autenticación y errores
                .requestMatchers("/api/v1/auth/**", "/error/**").permitAll()

                // gestión de usuarios: solo ADMIN puede acceder
                .requestMatchers("/usuarios/**").hasAuthority(Role.ADMIN.name())

                // compras
                // COMPRADOR ve sus propias compras
                .requestMatchers(HttpMethod.GET, "/compras/mias").hasAuthority(Role.COMPRADOR.name())
                // ADMIN ve todas las compras
                .requestMatchers(HttpMethod.GET, "/compras").hasAuthority(Role.ADMIN.name())

                // categorías
                // Solo ADMIN puede crear
                .requestMatchers("/categories/create").hasAuthority(Role.ADMIN.name())
                // Todos pueden ver categorías
                .requestMatchers("/categories/**").permitAll()

                // productos
                // ADMIN puede crear, modificar,activar y eliminar
                .requestMatchers(HttpMethod.POST, "/productos").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/productos/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/productos/activar/**").hasAuthority(Role.ADMIN.name())
                // Cualquier persona puede ver productos y filtrarlos
                .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()

                //Cambio de contraseña
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/change-password").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/admin/change-password").hasAuthority(Role.ADMIN.name())

                // 🔐 Todo lo que no esté especificado, requiere estar autenticado
                .anyRequest().authenticated()

            )

            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
