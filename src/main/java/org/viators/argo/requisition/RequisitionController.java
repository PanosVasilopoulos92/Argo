package org.viators.argo.requisition;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.requisition.dto.request.CreateRequisitionRequest;
import org.viators.argo.requisition.dto.response.RequisitionDetailsResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/requisitions")
@RequiredArgsConstructor
public class RequisitionController {

    private final RequisitionService requisitionService;

    @PreAuthorize("hasAnyRole('FOM', 'PROCUREMENT_CLERK')")
    @PostMapping
    public ResponseEntity<RequisitionDetailsResponse> create(
        @CurrentKeycloakId String keycloakId,
        @Valid @RequestBody CreateRequisitionRequest request
    ) {
        RequisitionDetailsResponse response = requisitionService.create(keycloakId, request);
        return ResponseEntity
            .created(URI.create("/api/v1/requisitions/" + response.reqPublicId()))
            .body(response);
    }
}
