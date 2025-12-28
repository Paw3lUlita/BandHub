package com.bandhub.zsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Włączamy CORS (korzysta z definicji poniżej)
                .cors(Customizer.withDefaults())

                // 2. Wyłączamy CSRF (niepotrzebne przy Stateless REST API)
                .csrf(csrf -> csrf.disable())

                // 3. Konfiguracja uprawnień
                .authorizeHttpRequests(auth -> auth
                        // Endpointy publiczne (np. Swagger, jeśli masz)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Endpoint OPTIONS (ważne dla CORS preflight w niektórych wersjach)
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // Nasz endpoint produktów - wymaga bycia zalogowanym
                        .requestMatchers("/api/admin/**").authenticated()

                        // Reszta też wymaga logowania
                        .anyRequest().authenticated()
                )

                // 4. Walidacja tokenów JWT (Keycloak)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ZEZWÓL NA ANGULARA (Port 4200):
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // ZEZWÓL NA METODY HTTP:
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // ZEZWÓL NA NAGŁÓWKI (Szczególnie Authorization!):
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));

        // Pozwól przesyłać ciasteczka/credentials (często wymagane):
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}