package org.viators.argo.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.auth.dto.CreateUserRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final KeycloakAdminService keycloakAdminService;

    /**
     * Registers a new user in Keycloak and saves their profile locally.
     *
     * @param request validated registration payload
     * @return 201 Created -- the client then logs in via Keycloak
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CreateUserRequest request) {
        keycloakAdminService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
