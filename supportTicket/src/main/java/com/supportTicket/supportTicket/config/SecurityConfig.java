package com.supportTicket.supportTicket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /**
         * MAIN SECURITY CONFIGURATION
         * --------------------------------
         * - Stateless JWT authentication
         * - Public and protected routes
         * - Role-based authorization
         */
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        CorsConfigurationSource corsConfigurationSource,
                        AuthenticationProvider authenticationProvider,
                        JwtFilter jwtFilter) throws Exception {

                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // PUBLIC ENDPOINTS
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/actuator/health",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/uploads/**")
                                                .permitAll()

                                                // ADMIN ONLY ENDPOINTS
                                                .requestMatchers(HttpMethod.POST, "/api/category/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/place/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/place/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/place/**").hasRole("ADMIN")
                                                // ANY OTHER REQUEST REQUIRES AUTH
                                                .anyRequest().authenticated())

                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        /**
         * PASSWORD ENCODER
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * AUTHENTICATION PROVIDER
         */
        @Bean
        public AuthenticationProvider authenticationProvider(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        /**
         * AUTHENTICATION MANAGER
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}