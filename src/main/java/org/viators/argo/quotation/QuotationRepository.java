package org.viators.argo.quotation;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface QuotationRepository extends JpaRepository<QuotationT, Long>, JpaSpecificationExecutor<QuotationT> {

    @EntityGraph(attributePaths = {"line", "supplier"})
    Optional<QuotationT> findByPublicId(String publicId);

    @Override
    @EntityGraph(attributePaths = {"line", "supplier"})
    Page<QuotationT> findAll(@NonNull Specification<QuotationT> spec, @NonNull Pageable pageable);

    @Query("select q from QuotationT q")
    @EntityGraph(attributePaths = {"supplier", "line"})
    Page<QuotationT> findAllForSummary(Pageable pageable);

    @Query("""
        select q from QuotationT q
        where q.reqLine.publicId = :linePublicId
        order by q.quotationState
        """)
    @EntityGraph(attributePaths = {"line", "supplier"})
    List<QuotationT> findAllQuotationsForReqLine(@Param("linePublicId") String linePublicId);

    @Query("""
           select q from QuotationT q
           left join fetch q.supplier s
           left join fetch q.reqLine rl
           left join fetch rl.quotations rlq
           where q.publicId in :quotationPublicIds
           """)
    Set<QuotationT> findQuotationsForPO(Set<String> quotationPublicIds);


    List<QuotationT> findByIdIn(Set<Long> ids);
}
