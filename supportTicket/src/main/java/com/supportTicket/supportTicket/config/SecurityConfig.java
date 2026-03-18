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
         * Main Spring Security filter chain for the application.
         * - Enables CORS using the provided CorsConfigurationSource bean.
         * - Disables CSRF for a stateless REST API.
         * - Sets session policy to STATELESS (JWT-based).
         * - Exposes some public endpoints (auth, swagger, health).
         * - Requires authentication for any other endpoint.
         * - Registers the AuthenticationProvider (DAO) and adds the JwtFilter before
         * UsernamePasswordAuthenticationFilter.
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
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/actuator/health",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/uploads/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/category").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        /**
         * Password encoder used to verify credentials against hashed passwords stored
         * in DB.
         * IMPORTANT: Ensure your users' passwords are stored using this encoder
         * (BCrypt).
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * DaoAuthenticationProvider configured with your UserDetailsService from DB and
         * the PasswordEncoder.
         * NOTE: Your version requires the constructor that accepts UserDetailsService.
         */
        @Bean
        public AuthenticationProvider authenticationProvider(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                // Your version expects this constructor; setter-based configuration may not
                // exist
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        /**
         * AuthenticationManager built by Spring using the configured provider(s).
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}