package com.medflow.appointmentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/// Configures HTTP security: stateless, CSRF-disabled, HTTP Basic
/// authentication with role-based authorization on the appointments API.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyRole("DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/**").hasAnyRole("DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/**").hasAnyRole("DOCTOR", "NURSE")

                        .requestMatchers(HttpMethod.GET, "/api/appointments/**")
                        .hasAnyRole("DOCTOR", "NURSE", "PATIENT")

                        .anyRequest().authenticated()
                )

                .httpBasic(basic -> {
                });

        return http.build();
    }
}
