package org.viators.argo.person.seafarer;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeafarerRepository extends JpaRepository<SeafarerT, Long>, JpaSpecificationExecutor<SeafarerT> {

    @EntityGraph(attributePaths = {"assignments"})
    Optional<SeafarerT> findByPassportNumber(String passportNumber);

    Optional<SeafarerT> findByPublicId(String publicId);

    Optional<SeafarerT> findBySeamanBookNumber(String seamanBookNumber);

    boolean existsBySeamanBookNumber(String seamanBookNumber);

    @Query("""
           select s from SeafarerT s
           left join fetch s.assignments a
           where s.publicId = :publicId
           order by a.assignmentState asc
           """)
    Optional<SeafarerT> findByPublicIdWithAssignments(@Param("publicId") String publicId);
}
