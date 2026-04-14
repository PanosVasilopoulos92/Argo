package org.viators.argo.certificate.person;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.certificate.person.dto.request.CreatePersonCertificateRequest;
import org.viators.argo.certificate.person.dto.response.PersonCertificateSummaryResponse;

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

    @GetMapping("/person/{personPublicId}")
    public ResponseEntity<Page<PersonCertificateSummaryResponse>> getCertificationsOfPerson(
        @PathVariable String personPublicId,
        @PageableDefault(sort = "expiryDate") Pageable pageable) {

        Page<PersonCertificateSummaryResponse> response =
            personCertificateService.getCertificatesForPerson(personPublicId, pageable);

        return ResponseEntity.ok(response);
    }
}
