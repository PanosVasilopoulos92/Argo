package org.viators.argo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserT, Long> {

    Optional<UserT> findByPublicId(String publicId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
