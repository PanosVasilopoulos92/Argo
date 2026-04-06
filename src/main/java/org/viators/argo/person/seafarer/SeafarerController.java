package org.viators.argo.person.seafarer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.person.seafarer.dto.request.CreateSeafarerRequest;
import org.viators.argo.person.seafarer.dto.request.SeafarerSearchFilterRequest;
import org.viators.argo.person.seafarer.dto.response.SeafarerDetailsResponse;
import org.viators.argo.person.seafarer.dto.response.SeafarerSummaryResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/seafarers")
@RequiredArgsConstructor
public class SeafarerController {

    private final SeafarerService seafarerService;

    @PreAuthorize("hasRole('FOM')")
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateSeafarerRequest request) {
        String publicId = seafarerService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/seafarers/" + publicId))
            .body(publicId);
    }

    @GetMapping("/passport/{passportNumber}")
    public ResponseEntity<SeafarerDetailsResponse> getByPassportNumber(@PathVariable String passportNumber) {
        SeafarerDetailsResponse response = seafarerService.getByPassportNumber(passportNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pid/{publicId}")
    public ResponseEntity<SeafarerDetailsResponse> getByPublicId(@PathVariable String publicId) {
        SeafarerDetailsResponse response = seafarerService.getByPublicId(publicId);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Page<SeafarerSummaryResponse>> getSeafarersFiltered(
        @Valid @RequestBody SeafarerSearchFilterRequest request,
        @PageableDefault(sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable
        ) {

        Page<SeafarerSummaryResponse> response = seafarerService.getSeafarersFiltered(request, pageable);
        return ResponseEntity.ok(response);
    }
}
