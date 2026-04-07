package org.viators.argo.person.seafarer;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.person.PersonRepository;
import org.viators.argo.person.seafarer.dto.request.CreateSeafarerRequest;
import org.viators.argo.person.seafarer.dto.request.SeafarerSearchFilterRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchBankDetailsRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchPassportRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchPersonalInfoRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchRankRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchRemarksRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchSeamanBookRequest;
import org.viators.argo.person.seafarer.dto.response.SeafarerDetailsResponse;
import org.viators.argo.person.seafarer.dto.response.SeafarerSummaryResponse;

import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeafarerService {

    private final PersonRepository personRepository;
    private final SeafarerRepository seafarerRepository;

    @Transactional
    public String create(CreateSeafarerRequest request) {

        if (personRepository.existsByPassportNumber(request.passportNumber())) {
            throw new DuplicateResourceException("Seafarer", "passportNumber", request.passportNumber());
        }

        if (seafarerRepository.existsBySeamanBookNumber(request.seamanBookNumber())) {
            throw new DuplicateResourceException("Seafarer", "seamanBookNumber", request.seamanBookNumber());
        }

        SeafarerT seafarer = request.toEntity();
        seafarer = seafarerRepository.save(seafarer);

        return seafarer.getPublicId();
    }

    @Transactional
    public SeafarerDetailsResponse patchPersonalInfo(String publicId, PatchPersonalInfoRequest request) {
        return executeUpdate(publicId, request.getVersion(), request::update);
    }

    @Transactional
    public SeafarerDetailsResponse patchPassport(String publicId, PatchPassportRequest request) {
        SeafarerT seafarer = loadResourceAndCheckVersion(publicId, request.getVersion());

        if (request.getPassportNumber().isPresent()) {
            String newPassportNumber = request.getPassportNumber().get();
            seafarerRepository.findByPassportNumber(newPassportNumber)
                .filter(existing -> !existing.getPublicId().equals(publicId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Seafarer", "passportNumber", newPassportNumber);
                });
        }

        request.update(seafarer);
        return SeafarerDetailsResponse.from(seafarerRepository.save(seafarer));
    }

    @Transactional
    public SeafarerDetailsResponse patchSeamanBook(String publicId, PatchSeamanBookRequest request) {
        SeafarerT seafarer = loadResourceAndCheckVersion(publicId, request.getVersion());

        if (request.getSeamanBookNumber().isPresent()) {
            String newSeamanBookNumber = request.getSeamanBookNumber().get();
            seafarerRepository.findBySeamanBookNumber(newSeamanBookNumber)
                .filter(existing -> !existing.getPublicId().equals(publicId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Seafarer", "seamanBookNumber", newSeamanBookNumber);
                });
        }

        request.update(seafarer);
        return SeafarerDetailsResponse.from(seafarerRepository.save(seafarer));
    }

    @Transactional
    public SeafarerDetailsResponse patchBankDetails(String publicId, PatchBankDetailsRequest request) {
        return executeUpdate(publicId, request.getVersion(), request::update);
    }

    @Transactional
    public SeafarerDetailsResponse patchRemarks(String publicId, PatchRemarksRequest request) {
        return executeUpdate(publicId, request.getVersion(), request::update);
    }

    @Transactional
    public SeafarerDetailsResponse patchRank(String publicId, PatchRankRequest request) {
        return executeUpdate(publicId, request.getVersion(), request::update);
    }

    @Transactional(readOnly = true)
    public SeafarerDetailsResponse getByPassportNumber(String passportNumber) {
        SeafarerT seafarer = seafarerRepository.findByPassportNumber(passportNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Seafarer", "passportNumber", passportNumber));

        return SeafarerDetailsResponse.from(seafarer);
    }

    @Transactional(readOnly = true)
    public SeafarerDetailsResponse getByPublicId(String publicId) {
        SeafarerT seafarer = seafarerRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Seafarer", "publicId", publicId));

        return SeafarerDetailsResponse.from(seafarer);
    }

    @Transactional(readOnly = true)
    public Page<SeafarerSummaryResponse> getSeafarersFiltered(SeafarerSearchFilterRequest filter, Pageable pageable) {
        Specification<SeafarerT> specs = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(filter.lastNameContaining())) {
            specs = specs.and(SeafarerSpecs.hasLastName(filter.lastNameContaining()));
        }

        if (filter.rank() != null) {
            specs = specs.and(SeafarerSpecs.hasRank(filter.rank()));
        }

        if (StringUtils.hasText(filter.nationality())) {
            specs = specs.and(SeafarerSpecs.hasNationality(filter.nationality()));
        }

        if (!filter.includeInactiveSeafarers()) {
            specs = specs.and(SeafarerSpecs.isActive());
        }

        return seafarerRepository.findAll(specs, pageable)
            .map(SeafarerSummaryResponse::from);
    }


    // Private Helper methods
    private SeafarerDetailsResponse executeUpdate(String publicId, Long expectedVersion, Consumer<SeafarerT> updater) {
        SeafarerT seafarer = loadResourceAndCheckVersion(publicId, expectedVersion);

        updater.accept(seafarer);
        return SeafarerDetailsResponse.from(seafarerRepository.save(seafarer));
    }

    private SeafarerT loadResourceAndCheckVersion(String publicId, Long expectedVersion) {
        SeafarerT seafarer = seafarerRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Seafarer", "publicId", publicId));

        if (!Objects.equals(seafarer.getVersion(), expectedVersion)) {
            throw new OptimisticLockException("Seafarer was modified by another user. Please reload and try again.");
        }

        return seafarer;
    }

}
