package org.viators.argo.certificate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.certificate.dto.request.UpdateCertificateRequest;
import org.viators.argo.certificate.dto.response.CertificateDetailsResponse;
import org.viators.argo.certificate.dto.response.CertificateOverviewResponse;
import org.viators.argo.common.enums.ReportPeriod;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{certificatePublicId}")
    public ResponseEntity<CertificateDetailsResponse> update(
        @PathVariable String certificatePublicId,
        @Valid @RequestBody UpdateCertificateRequest request
    ) {
        CertificateDetailsResponse response = certificateService.updateCertificate(certificatePublicId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CertificateOverviewResponse>> getCertificatesExpiringAt(
        @RequestParam ReportPeriod daysUntilExpiry,
        @PageableDefault(sort = "expiryDate") Pageable pageable
    ) {
        int days = daysUntilExpiry.getDays();
        Page<CertificateOverviewResponse> response = certificateService.getCertifications(days, pageable);
        return ResponseEntity.ok(response);
    }
}
