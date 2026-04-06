package org.viators.argo.auth;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.auth.dto.CreateUserRequest;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.config.KeycloakAdminProperties;
import org.viators.argo.user.UserRepository;
import org.viators.argo.user.UserRoleEnum;
import org.viators.argo.user.UserT;

import java.util.List;

/**
 * Handles user registration by creating accounts in Keycloak
 * and persisting application-specific data locally.
 * <p>
 * Registration flow:
 * 1. Validate -- check local DB for duplicate username/email.
 * 2. Create -- call Keycloak Admin API to create the user.
 * 3. Role -- assign the USER realm role in Keycloak.
 * 4. Link -- save local profile data with the Keycloak UUID as a foreign key.
 * <p>
 * Passwords are managed entirely by Keycloak. This service never
 * hashes or stores passwords.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final UserRepository userRepository;
    private final KeycloakAdminProperties keycloakAdminProperties;

    @Transactional
    public void registerUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already taken: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered: " + request.email());
        }

        // Keycloak Admin API interaction.
        // try-with-resources: Keycloak implements AutoCloseable.
        try (Keycloak keycloakAdmin = buildKeycloakAdmin()) {

            // Build the Keycloak user representation
            var userRepresentation = getUserRepresentation(request);

            // Create the user in Keycloak
            var usersResource = keycloakAdmin.realm(keycloakAdminProperties.getRealm()).users();
            Response response = usersResource.create(userRepresentation);

            if (response.getStatus() != 201) {
                String body = response.readEntity(String.class);
                log.error("Keycloak user creation failed — status: {}, body: {}",
                    response.getStatus(), body);
                throw new RuntimeException("User registration failed. Please try again.");
            }

            // Extract the new user's Keycloak UUID from the Location header.
            // Location: http://keycloak:8080/admin/realms/argo-realm/users/{uuid}
            String location = response.getHeaderString("Location");
            String keycloakId = location.substring(location.lastIndexOf("/") + 1);

            // Assign the role in Keycloak
            var userResource = usersResource.get(keycloakId);
            var userRole = keycloakAdmin.realm(keycloakAdminProperties.getRealm())
                .roles()
                .get("USER")
                .toRepresentation();

            userResource.roles().realmLevel().add(List.of(userRole));

            // Save local application data.
            // keycloakId links this record to Keycloak — use it to look
            // up the local user when you have a JWT with the "sub" claim.
            var localUser = UserT.builder()
                .username(request.username())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .dateOfBirth(request.dateOfBirth())
                .keycloakId(keycloakId)
                .userRole(UserRoleEnum.USER)
                .status(ResourceStatusEnum.ACTIVE)
                .build();

            userRepository.save(localUser);
            log.info("User registered: username={}, keycloakId={}", request.username(), keycloakId);
        }
    }

    private static @NonNull UserRepresentation getUserRepresentation(CreateUserRequest request) {
        var credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        var userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setEmail(request.email());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true); // Skip email verification in dev
        userRepresentation.setCredentials(List.of(credential));
        return userRepresentation;
    }

    /**
     * Builds an authenticated Keycloak admin client using client credentials.
     * <p>
     * The client_credentials grant type authenticates as the application
     * itself (not as a user), giving it admin access to create users.
     * The caller must close this client — hence always use try-with-resources.
     */
    private Keycloak buildKeycloakAdmin() {
        return KeycloakBuilder.builder()
            .serverUrl(keycloakAdminProperties.getServerUrl())
            .realm(keycloakAdminProperties.getRealm())
            .clientId(keycloakAdminProperties.getClientId())
            .clientSecret(keycloakAdminProperties.getClientSecret())
            .grantType("client_credentials")
            .build();
    }
}
