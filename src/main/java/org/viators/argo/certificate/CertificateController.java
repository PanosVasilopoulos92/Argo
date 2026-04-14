package org.viators.argo.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.certificate.dto.response.CertificateOverviewResponse;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<Page<CertificateOverviewResponse>> getAllCertificates(
        @PageableDefault(sort = "expiryDate") Pageable pageable
    ) {

        Page<CertificateOverviewResponse> response = certificateService.getCertifications(pageable);
        return ResponseEntity.ok(response);
    }
}
