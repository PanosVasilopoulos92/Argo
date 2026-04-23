package org.viators.argo.person;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PersonQueryService {

    private final PersonRepository personRepository;

    public PersonT getPersonByPublicId(String publicId) {
        return personRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Person", "publicId", publicId));
    }

    public PersonT getPersonByDatabaseId(Long personId) {
        return personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person", "Id", personId));
    }

}
