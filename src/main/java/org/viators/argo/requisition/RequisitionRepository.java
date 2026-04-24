package org.viators.argo.requisition;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequisitionRepository extends JpaRepository<RequisitionT, Long>, JpaSpecificationExecutor<RequisitionT> {

    @EntityGraph(attributePaths = {"lines"})
    Optional<RequisitionT> findByPublicId(String publicId);

    @Override
    @EntityGraph(attributePaths = {"targetVessel", "raisedBy"})
    Page<RequisitionT> findAll(@NonNull Specification<RequisitionT> spec, @NonNull Pageable pageable);
}
