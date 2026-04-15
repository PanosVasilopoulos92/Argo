package org.viators.argo.certificate.vessel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.certificate.vessel.dto.request.CreateVesselCertificateRequest;
import org.viators.argo.certificate.vessel.dto.response.VesselCertificateSummaryResponse;

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
            .created(URI.create("/api/v1/vessel-certifications/" + vesselCertificationPublicId))
            .body(vesselCertificationPublicId);
    }

    @GetMapping("/vessel/{vesselPublicId}")
    public ResponseEntity<Page<VesselCertificateSummaryResponse>> getCertificatesForVessel(
        @PathVariable String vesselPublicId,
        @PageableDefault(sort = "expiryDate") Pageable pageable
    ) {
        Page<VesselCertificateSummaryResponse> response =
            vesselCertificateService.getCertificatesForVessel(vesselPublicId, pageable);

        return ResponseEntity.ok(response);
    }
}
