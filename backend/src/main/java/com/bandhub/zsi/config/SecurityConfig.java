package com.bandhub.zsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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

                        // endpoint publiczny dla składania zamówień
                        .requestMatchers("/api/public/**").permitAll()

                        // Reszta też wymaga logowania
                        .anyRequest().authenticated()
                )

                // 4. Walidacja tokenów JWT (Keycloak)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

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

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        scopeAuthoritiesConverter.setAuthorityPrefix("SCOPE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(scopeAuthoritiesConverter.convert(jwt));
            authorities.addAll(extractRealmRoles(jwt));
            return authorities;
        });
        return converter;
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return List.of();
        }

        Object rolesClaim = realmAccess.get("roles");
        if (!(rolesClaim instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .flatMap(this::toRoleAuthorities)
                .toList();
    }

    private Stream<GrantedAuthority> toRoleAuthorities(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return Stream.empty();
        }

        if (roleName.startsWith("ROLE_")) {
            return Stream.of(new SimpleGrantedAuthority(roleName));
        }

        return Stream.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }
}