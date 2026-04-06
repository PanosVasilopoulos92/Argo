package org.viators.argo.person.seafarer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeafarerRepository extends JpaRepository<SeafarerT, Long>, JpaSpecificationExecutor<SeafarerT> {

    Optional<SeafarerT> findByPassportNumber(String passportNumber);

    Optional<SeafarerT> findByPublicId(String publicId);

    boolean existsBySeamanBookNumber(String seamanBookNumber);

}
