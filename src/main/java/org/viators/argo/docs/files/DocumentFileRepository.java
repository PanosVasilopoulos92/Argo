package org.viators.argo.docs.files;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFileT, Long> {

    Optional<DocumentFileT> findByStorageKey(String storageKey);
}
