package org.viators.argo.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemT, Long>, JpaSpecificationExecutor<ItemT> {

    Optional<ItemT> findByPublicId(String publicId);

    Optional<ItemT> findByPartNumberAndManufacturer(String partNumber, String manufacturer);
}
