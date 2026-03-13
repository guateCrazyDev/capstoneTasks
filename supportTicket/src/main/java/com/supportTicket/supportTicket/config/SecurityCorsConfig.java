package com.supportTicket.supportTicket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityCorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Usa orígenes explícitos (no "*") cuando allowCredentials = true
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Vite
        // Si usas Spring Framework 6 / Boot 3 y tienes problemas con setAllowedOrigins
        // puedes usar:
        // config.setAllowedOriginPatterns(List.of("http://localhost:5173"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true); // <-- Imprescindible si usas withCredentials
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}