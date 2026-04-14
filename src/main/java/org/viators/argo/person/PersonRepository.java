package org.viators.argo.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<PersonT, Long> {

    boolean existsByPassportNumber(String passportNumber);

    Optional<PersonT> findByPublicId(String publicId);
}
