package org.viators.argo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Configures how Spring Security extracts roles from Keycloak JWTs.
 *
 * Keycloak stores realm roles at realm_access.roles.
 * Spring's default looks at the scope claim. This converter
 * bridges that mismatch.
 *
 * After conversion, Spring Security sees:
 *   - ROLE_USER from Keycloak's "USER"
 *   - ROLE_ADMIN from Keycloak's "ADMIN"
 *
 * Which means hasRole("USER") and @PreAuthorize("hasRole('ADMIN')")
 * work exactly as you'd expect — Spring checks for ROLE_ prefix internally.
 */
@Configuration
public class JwtConverterConfig {

    /**
     * Creates the JWT-to-Authentication converter for Keycloak tokens.
     *
     * Two converters compose here:
     *   1. JwtGrantedAuthoritiesConverter -- reads a claim and converts
     *      each value to a GrantedAuthority with a prefix.
     *   2. JwtAuthenticationConverter -- orchestrates full token conversion:
     *      extracts the principal name and delegates to the authorities
     *      converter for permissions.
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        converter.setPrincipalClaimName("preferred_username");

        return converter;
    }
}
