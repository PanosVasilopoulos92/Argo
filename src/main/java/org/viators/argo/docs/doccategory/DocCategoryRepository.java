package org.viators.argo.docs.doccategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocCategoryRepository extends JpaRepository<DocCategoryT, Long> {

    Optional<DocCategoryT> findByPublicId(String publicId);

    boolean existsByIdAndDocumentFilesNotEmpty(Long id);

    boolean existsByName(String name);
}
