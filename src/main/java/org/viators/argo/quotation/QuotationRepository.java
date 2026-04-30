package org.viators.argo.quotation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<QuotationT, Long> {

    @EntityGraph(attributePaths = {"line", "supplier"})
    Optional<QuotationT> findByPublicId(String publicId);
}
