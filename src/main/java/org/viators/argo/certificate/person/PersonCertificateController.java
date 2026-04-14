package org.viators.argo.certificate.person;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.certificate.person.dto.request.CreatePersonCertificateRequest;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/person-certificates")
@RequiredArgsConstructor
public class PersonCertificateController {

    private final PersonCertificateService personCertificateService;

    @PreAuthorize("hasRole('FOM')")
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreatePersonCertificateRequest request) {
        String personCertificatePublicId = personCertificateService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/person-certificates" + personCertificatePublicId))
            .body(personCertificatePublicId);
    }

}
