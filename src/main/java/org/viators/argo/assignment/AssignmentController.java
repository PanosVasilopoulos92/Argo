package org.viators.argo.assignment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.assignment.dto.request.CreateAssignmentRequest;
import org.viators.argo.assignment.dto.request.SignOffSeafarerRequest;
import org.viators.argo.assignment.dto.response.AssignmentDetailsResponse;
import org.viators.argo.assignment.dto.response.AssignmentsHistOfSeafarerResponse;
import org.viators.argo.assignment.dto.response.AssignmentsHistOfVesselResponse;
import org.viators.argo.assignment.dto.response.CrewRosterResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PreAuthorize("hasRole('FOM')")
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateAssignmentRequest request) {
        String createdAssignmentPublicId = assignmentService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/assignments/" + createdAssignmentPublicId))
            .body(createdAssignmentPublicId);
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/sign-off")
    public ResponseEntity<AssignmentDetailsResponse> signOffSeafarer(
        @PathVariable String publicId,
        @Valid @RequestBody SignOffSeafarerRequest request) {

        AssignmentDetailsResponse response = assignmentService.signOffSeafarer(publicId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/crew-roster/{vesselPublicId}")
    public ResponseEntity<Page<CrewRosterResponse>> getCurrentCrewRosterForVessel(
        @PathVariable String vesselPublicId,
        @PageableDefault(sort = "assignmentRank", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<CrewRosterResponse> response = assignmentService.getCurrentCrewRosterForVessel(vesselPublicId, pageable);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('FOM')")
    @DeleteMapping("/{assignmentPublicId}")
    public ResponseEntity<Void> cancelAssignment(@PathVariable String assignmentPublicId) {
        assignmentService.cancelAssignment(assignmentPublicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assignments-history/seafarer/{seafarerPublicId}/")
    public ResponseEntity<Page<AssignmentsHistOfSeafarerResponse>> getSeafarerAssignmentsHist(
        @PathVariable() String seafarerPublicId,
        @PageableDefault Pageable pageable
    ) {
        Page<AssignmentsHistOfSeafarerResponse> response = assignmentService.getAssignmentsHistForSeafarer(
            seafarerPublicId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/assignments-history/vessel/{vesselPublicId}/")
    public ResponseEntity<Page<AssignmentsHistOfVesselResponse>> getVesselAssignmentsHist(
        @PathVariable() String vesselPublicId,
        @PageableDefault Pageable pageable
    ) {
        Page<AssignmentsHistOfVesselResponse> response = assignmentService.getAssignmentsHistForVessel(
            vesselPublicId, pageable);

        return ResponseEntity.ok(response);
    }
}
