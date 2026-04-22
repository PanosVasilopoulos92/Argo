package org.viators.argo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserT getUser(String keycloakId) {
        UserT user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "publicId", keycloakId));

        if (user.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("User with keycloak Id: %s is currently inactive"
                .formatted(keycloakId));
        }

        return user;
    }
}
