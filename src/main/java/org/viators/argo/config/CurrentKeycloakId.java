package org.viators.argo.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects the Keycloak user id (the JWT "sub" claim) into a String controller parameter.
 * Shorthand for @AuthenticationPrincipal(expression = "subject"), so the SpEL
 * expression lives in one place rather than being repeated in every controller method.
 * Apply to a String parameter on a controller method to receive the authenticated
 * user's Keycloak id directly, without unwrapping the Jwt principal manually.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "subject")
public @interface CurrentKeycloakId {
}
