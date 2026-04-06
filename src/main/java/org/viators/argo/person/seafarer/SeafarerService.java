package org.viators.argo.person.seafarer;

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
import org.viators.argo.person.seafarer.dto.response.SeafarerDetailsResponse;
import org.viators.argo.person.seafarer.dto.response.SeafarerSummaryResponse;

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
}
