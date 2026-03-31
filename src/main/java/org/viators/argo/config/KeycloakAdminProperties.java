package org.viators.argo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds Keycloak admin client properties from application.yml.
 *
 * Used by KeycloakAdminService to create users in Keycloak
 * during registration. The client secret must always come from an
 * environment variable — never hardcoded.
 *
 * Properties bound from the keycloak.admin.* namespace:
 *   - keycloak.admin.server-url
 *   - keycloak.admin.realm
 *   - keycloak.admin.client-id
 *   - keycloak.admin.client-secret
 */
@Component
@ConfigurationProperties(prefix = "keycloak.admin")
@Getter
@Setter
public class KeycloakAdminProperties {

    /** Keycloak server base URL. */
    private String serverUrl;

    /** The realm where application users are managed. */
    private String realm;

    /** Client ID registered in Keycloak for this application. */
    private String clientId;

    /**
     * Client secret from Keycloak's Credentials tab.
     * Injected from the KEYCLOAK_CLIENT_SECRET environment variable.
     */
    private String clientSecret;
}
