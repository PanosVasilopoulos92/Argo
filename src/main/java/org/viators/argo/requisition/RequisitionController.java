package org.viators.argo.requisition;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.config.CurrentKeycloakId;
import org.viators.argo.requisition.dto.request.*;
import org.viators.argo.requisition.dto.response.ReqDetailsWithRelationshipsSummaryResponse;
import org.viators.argo.requisition.dto.response.RequisitionDetailsResponse;
import org.viators.argo.requisition.dto.response.RequisitionSummaryResponse;

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
    @PatchMapping("/{reqPublicId}/submit")
    public ResponseEntity<RequisitionDetailsResponse> submitRequisition(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String reqPublicId,
        @Valid @RequestBody SubmitRequisitionRequest request
    ) {
        RequisitionDetailsResponse response = requisitionService.submitRequisition(
            keycloakId, reqPublicId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reqPublicId}/approve")
    public ResponseEntity<RequisitionDetailsResponse> approveRequisition(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String reqPublicId,
        @Valid @RequestBody ApproveRequisitionRequest request
    ) {
        RequisitionDetailsResponse response = requisitionService.approveRequisition(
            keycloakId, reqPublicId, request);

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{reqPublicId}/reject")
    public ResponseEntity<RequisitionDetailsResponse> rejectRequisition(
        @CurrentKeycloakId String keycloakId,
        @PathVariable String reqPublicId,
        @Valid @RequestBody RejectRequisitionRequest request
    ) {
        RequisitionDetailsResponse response = requisitionService.rejectRequisition(
            keycloakId, reqPublicId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<RequisitionSummaryResponse>> getRequisitionsFiltered(
        @ModelAttribute RequisitionSearchFilterRequest request,
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RequisitionSummaryResponse> response = requisitionService.getRequisitionFiltered(
            request, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reqPublicId}")
    public ResponseEntity<ReqDetailsWithRelationshipsSummaryResponse> getRequisitionDetailsWithRelationshipsSummary(
        @PathVariable String reqPublicId
    ) {
        return ResponseEntity.ok(requisitionService.getRequisitionDetailsWithRelationshipsSummary(reqPublicId));
    }

}
