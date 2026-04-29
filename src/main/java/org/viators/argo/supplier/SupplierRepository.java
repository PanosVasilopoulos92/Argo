package org.viators.argo.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierT, Long>, JpaSpecificationExecutor<SupplierT> {

    Optional<SupplierT> findByPublicId(String publicId);

    Optional<SupplierT> findByEmail(String email);

    Optional<SupplierT> findByVatNumber(String vatNumber);

    boolean existsByEmail(String email);

    boolean existsByVatNumber(String vatNumber);
}
