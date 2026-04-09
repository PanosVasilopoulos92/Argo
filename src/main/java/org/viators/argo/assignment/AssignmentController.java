package org.viators.argo.assignment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.assignment.dto.request.CreateAssignmentRequest;
import org.viators.argo.assignment.dto.request.SignOffSeafarerRequest;
import org.viators.argo.assignment.dto.response.AssignmentDetailsResponse;

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

    @PatchMapping("/{publicId}")
    public ResponseEntity<AssignmentDetailsResponse> signOffSeafarer(
        @PathVariable String publicId,
        @Valid @RequestBody SignOffSeafarerRequest request) {

        AssignmentDetailsResponse response = assignmentService.signOffSeafarer(publicId, request);
        return ResponseEntity.ok(response);
    }
}
