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
            // 1. Stateless, sin CSRF
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))

            // 2. Reglas de acceso
            .authorizeHttpRequests(auth -> auth

                //   a) Público: auth, errores, catálogo y reviews (GET)
                .requestMatchers("/api/v1/auth/**", "/error/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()

                //   b) Reviews: POST solo COMPRADOR
                .requestMatchers(HttpMethod.POST, "/reviews")
                    .hasAuthority(Role.COMPRADOR.name())

                //   c) Compras
                .requestMatchers(HttpMethod.POST, "/compras")
                    .hasAuthority(Role.COMPRADOR.name())
                .requestMatchers(HttpMethod.GET, "/compras/mias")
                    .hasAuthority(Role.COMPRADOR.name())
                .requestMatchers(HttpMethod.GET, "/compras")
                    .hasAuthority(Role.ADMIN.name())

                //   d) Categorías
                .requestMatchers(HttpMethod.POST, "/categories/create")
                    .hasAuthority(Role.ADMIN.name())

                //   e) Gestión de usuarios
                .requestMatchers(HttpMethod.GET,  "/api/v1/users/me")
                    .authenticated()
                .requestMatchers("/api/v1/users/**")
                    .hasAuthority(Role.ADMIN.name())

                //   f) Cambios de contraseña
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/change-password")
                    .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/admin/change-password")
                    .hasAuthority(Role.ADMIN.name())

                //   g) Productos (CRUD solo ADMIN)
                .requestMatchers(HttpMethod.POST,   "/productos").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,    "/productos/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,    "/productos/activar/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,    "/productos/stock/**").hasAuthority(Role.ADMIN.name())
                // Reglas para favoritos
                 .requestMatchers("/api/v1/favoritos/**").authenticated()
                   // Reglas para descuentos - solo ADMIN puede gestionar y ver todos los descuentos
                .requestMatchers(HttpMethod.POST, "/descuentos").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/descuentos/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/descuentos/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/descuentos").hasAuthority(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/descuentos/**").hasAuthority(Role.ADMIN.name())
                
                // Solo la validación de descuentos es pública
                .requestMatchers(HttpMethod.POST, "/descuentos/validar").permitAll()
                
                //   h) Resto de rutas: autenticado
                .anyRequest().authenticated()
            )

            // 3. JWT filter
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
