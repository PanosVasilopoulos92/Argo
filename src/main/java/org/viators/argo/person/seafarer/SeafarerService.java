package org.viators.argo.person.seafarer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.person.PersonRepository;
import org.viators.argo.person.seafarer.dto.request.CreateSeafarerRequest;

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
}
