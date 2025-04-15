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

                // Públicos
                .requestMatchers("/api/v1/auth/**", "/error/**").permitAll()

                // Usuarios: solo ADMIN
                .requestMatchers("/usuarios/**").hasAuthority(Role.ADMIN.name())

                // Categorías
                .requestMatchers("/categories/create").hasAuthority(Role.ADMIN.name())
                .requestMatchers("/categories/**").authenticated()

                // Productos (lectura): COMPRADOR, VENDEDOR y ADMIN
                .requestMatchers(HttpMethod.GET, "/productos/**")
                    .hasAnyAuthority(Role.COMPRADOR.name(), Role.VENDEDOR.name(), Role.ADMIN.name())

                // Productos (creación, edición, borrado): solo VENDEDOR
                .requestMatchers(HttpMethod.POST, "/productos").hasAuthority(Role.VENDEDOR.name())
                .requestMatchers(HttpMethod.PUT, "/productos/**").hasAuthority(Role.VENDEDOR.name())
                .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAuthority(Role.VENDEDOR.name())

                // Cualquier otro endpoint requiere estar autenticado
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
