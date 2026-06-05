package org.viators.argo.docs.doccategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocCategoryRepository extends JpaRepository<DocCategoryT, Long> {

    Optional<DocCategoryT> findByPublicId(String publicId);

    @Query("""
           select c from DocCategoryT c
           left join fetch c.documentFiles df
           where c.publicId = :docCategoryPublicId
           order by c.updatedAt desc
           """)
    Optional<DocCategoryT> findByPublicIdWithDocFiles(@Param("docCategoryPublicId") String docCategoryPublicId);

    boolean existsByIdAndDocumentFilesNotEmpty(Long id);

    boolean existsByName(String name);
}
