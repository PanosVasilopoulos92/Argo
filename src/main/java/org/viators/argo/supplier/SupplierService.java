package org.viators.argo.supplier;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.supplier.dto.request.CreateSupplierRequest;
import org.viators.argo.supplier.dto.request.PatchSupplierInfo;
import org.viators.argo.supplier.dto.request.SearchSupplierFiltersRequest;
import org.viators.argo.supplier.dto.response.SupplierDetailsResponse;
import org.viators.argo.supplier.dto.response.SupplierSummaryResponse;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierDetailsResponse create(CreateSupplierRequest request) {
        SupplierT supplier = request.toEntity();
        validateUniquenessOfEmailAndVatNumberOnCreate(request.email(), request.vatNumber());
        supplierRepository.save(supplier);

        return SupplierDetailsResponse.from(supplier);
    }

    @Transactional
    public SupplierDetailsResponse updateInfo(String supplierPublicId, PatchSupplierInfo request) {
        SupplierT supplier = loadResourceAndCheckVersionAndStatus(supplierPublicId, request.getVersion());
        validateUniquenessOfEmailOnUpdate(supplier.getId(), request);
        request.update(supplier);

        return SupplierDetailsResponse.from(supplierRepository.save(supplier));
    }

    @Transactional
    public void deactivateSupplier(String publicId) {
        SupplierT supplier = supplierRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "publicId", publicId));

        if (supplier.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("Supplier with publicId: %s is already inactive"
                .formatted(publicId));
        }

        supplier.setStatus(ResourceStatusEnum.INACTIVE);
    }

    @Transactional
    public void reactivateSupplier(String publicId) {
        SupplierT supplier = supplierRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "publicId", publicId));

        if (supplier.getStatus().equals(ResourceStatusEnum.ACTIVE)) {
            throw new InvalidStateException("Supplier with publicId: %s is already active"
                .formatted(publicId));
        }

        supplier.setStatus(ResourceStatusEnum.ACTIVE);
    }

    // Read only methods
    @Transactional(readOnly = true)
    public SupplierDetailsResponse getByPublicId(String publicId) {
        SupplierT supplier = supplierRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "publicId", publicId));

        return SupplierDetailsResponse.from(supplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplierSummaryResponse> getByFilters(SearchSupplierFiltersRequest filters, Pageable pageable) {
        Specification<SupplierT> specs = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(filters.companyNameContaining())) {
            specs = specs.and(SupplierSpecs.hasCompNameContaining(filters.companyNameContaining()));
        }

        if (StringUtils.hasText(filters.vatNumber())) {
            specs = specs.and(SupplierSpecs.hasVatNumber(filters.vatNumber()));
        }

        if (StringUtils.hasText(filters.email())) {
            specs = specs.and(SupplierSpecs.hasEmail(filters.email()));
        }

        if (filters.status() != null) {
            specs = specs.and(SupplierSpecs.hasStatus(filters.status()));
        }

        return supplierRepository.findAll(specs, pageable)
            .map(SupplierSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public SupplierT getActiveResource(String supPublicId) {
        SupplierT supplier = supplierRepository.findByPublicId(supPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "publicId", supPublicId));

        if (supplier.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("Supplier with public Id: %s is inactive"
                .formatted(supPublicId));
        }

        return supplier;
    }

    // Private helper methods
    private void validateUniquenessOfEmailAndVatNumberOnCreate(String supEmail, String supVatNumber) {
        if (supplierRepository.existsByEmail(supEmail)) {
            throw new DuplicateResourceException("Supplier", "email", supEmail);
        }

        if (supplierRepository.existsByVatNumber(supVatNumber)) {
            throw new DuplicateResourceException("Supplier", "vatNumber", supVatNumber);
        }
    }

    private void validateUniquenessOfEmailOnUpdate(Long id, PatchSupplierInfo request) {
        if (request.getEmail().isPresent()) {
            String supEmail = request.getEmail().get();
            supplierRepository.findByEmail(supEmail)
                .filter(sup -> !sup.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Supplier", "email", supEmail);
                });
        }
    }

    private SupplierT loadResourceAndCheckVersionAndStatus(String resourcePublicId, Long requestVersion) {
        SupplierT supplier = supplierRepository.findByPublicId(resourcePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "publicId", resourcePublicId));

        if (supplier.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("Supplier with publicId: %s is inactive"
                .formatted(resourcePublicId));
        }

        if (!Objects.equals(supplier.getVersion(), requestVersion)) {
            throw new OptimisticLockException("Some other user has updated resource while you made your edit. Please try again");
        }

        return supplier;
    }
}
