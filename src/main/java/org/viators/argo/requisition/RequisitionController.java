package org.viators.argo.requisition;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.requisition.dto.request.CreateRequisitionRequest;
import org.viators.argo.requisition.dto.request.SubmitRequisitionRequest;
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
        RequisitionDetailsResponse response = requisitionService.createDraft(keycloakId, request);
        return ResponseEntity
            .created(URI.create("/api/v1/requisitions/" + response.reqPublicId()))
            .body(response);
    }

    @PreAuthorize("hasAnyRole('FOM', 'PROCUREMENT_CLERK')")
    @PostMapping("/{reqPublicId}/submit")
    public ResponseEntity<RequisitionDetailsResponse> submitRequisition(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String reqPublicId,
        @Valid @RequestBody SubmitRequisitionRequest request
    ) {
        RequisitionDetailsResponse response = requisitionService.submitRequisition(
            keycloakId, reqPublicId, request
        );

        return ResponseEntity.ok(response);
    }

}
