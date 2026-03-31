package org.viators.argo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central security configuration for the Argo backend.
 * <p>
 * This application is a pure OAuth2 Resource Server. It validates tokens
 * issued by Keycloak but never issues, stores, or manages them.
 * <p>
 * What Spring auto-configures from your pom + application.yml:
 * - BearerTokenAuthenticationFilter -- reads the Authorization header,
 * extracts the Bearer token, passes it to the JwtDecoder.
 * - NimbusJwtDecoder -- fetches Keycloak's public key from the JWKS URI,
 * verifies the RS256 signature, validates exp and iss claims.
 * <p>
 * You configure two things manually:
 * 1. Authorization rules -- which URLs require what roles.
 * 2. JwtConverterConfig -- how to extract roles from Keycloak's token structure.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConverterConfig jwtConverterConfig;

    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/register").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(
                        jwtConverterConfig.jwtAuthenticationConverter()
                    )
                )
            );

        return http.build();
    }

}
