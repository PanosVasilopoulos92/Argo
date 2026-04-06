package org.viators.argo.person.seafarer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeafarerRepository extends JpaRepository<SeafarerT, Long> {

    boolean existsBySeamanBookNumber(String seamanBookNumber);

}
