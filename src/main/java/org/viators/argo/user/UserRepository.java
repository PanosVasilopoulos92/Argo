package org.viators.argo.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserT, Long> {

    @EntityGraph(attributePaths = {"person"})
    Optional<UserT> findByKeycloakId(String keycloakId);

    Optional<UserT> findByPublicId(String publicId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
