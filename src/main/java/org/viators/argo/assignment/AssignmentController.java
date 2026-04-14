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
    @PatchMapping("/{publicId}")
    public ResponseEntity<AssignmentDetailsResponse> signOffSeafarer(
        @PathVariable String publicId,
        @Valid @RequestBody SignOffSeafarerRequest request) {

        AssignmentDetailsResponse response = assignmentService.signOffSeafarer(publicId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/crew-roaster/{vesselPublicId}")
    public ResponseEntity<Page<CrewRosterResponse>> getCurrentCrewRosterForVessel(
        @PathVariable String vesselPublicId,
        @PageableDefault(sort = "assignmentRank", direction = Sort.Direction.ASC) Pageable pageable
        ) {

        Page<CrewRosterResponse> response = assignmentService.getCurrentCrewRosterForVessel(vesselPublicId, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{assignmentPublicId}")
    public ResponseEntity<Void> cancelAssignment(@PathVariable String assignmentPublicId) {
        assignmentService.cancelAssignment(assignmentPublicId);
        return ResponseEntity.noContent().build();
    }
}
