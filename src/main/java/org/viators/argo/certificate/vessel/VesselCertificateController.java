package org.viators.argo.certificate.vessel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.certificate.vessel.dto.request.CreateVesselCertificateRequest;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/vessel-certifications")
@RequiredArgsConstructor
public class VesselCertificateController {

    private final VesselCertificateService vesselCertificateService;

    @PreAuthorize("hasRole('FOM')")
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateVesselCertificateRequest request) {
        String vesselCertificationPublicId = vesselCertificateService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/vessel-certifications" + vesselCertificationPublicId))
            .body(vesselCertificationPublicId);
    }
}
