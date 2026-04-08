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
import org.viators.argo.person.seafarer.dto.request.patch.PatchBankDetailsRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchPassportRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchPersonalInfoRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchRankRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchRemarksRequest;
import org.viators.argo.person.seafarer.dto.request.patch.PatchSeamanBookRequest;
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

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/personal-info")
    public ResponseEntity<SeafarerDetailsResponse> patchPersonalInfo(
        @PathVariable String publicId,
        @Valid @RequestBody PatchPersonalInfoRequest request
    ) {
        return ResponseEntity.ok(seafarerService.patchPersonalInfo(publicId, request));
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/passport")
    public ResponseEntity<SeafarerDetailsResponse> patchPassport(
        @PathVariable String publicId,
        @Valid @RequestBody PatchPassportRequest request
    ) {
        return ResponseEntity.ok(seafarerService.patchPassport(publicId, request));
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/seaman-book")
    public ResponseEntity<SeafarerDetailsResponse> patchSeamanBook(
        @PathVariable String publicId,
        @Valid @RequestBody PatchSeamanBookRequest request
    ) {
        return ResponseEntity.ok(seafarerService.patchSeamanBook(publicId, request));
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/bank-details")
    public ResponseEntity<SeafarerDetailsResponse> patchBankDetails(
        @PathVariable String publicId,
        @Valid @RequestBody PatchBankDetailsRequest request
    ) {
        return ResponseEntity.ok(seafarerService.patchBankDetails(publicId, request));
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/rank")
    public ResponseEntity<SeafarerDetailsResponse> patchRank(
        @PathVariable String publicId,
        @Valid @RequestBody PatchRankRequest request
    ) {
        return ResponseEntity.ok(seafarerService.patchRank(publicId, request));
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/remarks")
    public ResponseEntity<SeafarerDetailsResponse> patchRemarks(
        @PathVariable String publicId,
        @Valid @RequestBody PatchRemarksRequest request
    ) {
        return ResponseEntity.ok(seafarerService.patchRemarks(publicId, request));
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/deactivate")
    public ResponseEntity<Void> deactivateSeafarer(@PathVariable String publicId) {
        seafarerService.deactivateSeafarer(publicId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('FOM')")
    @PatchMapping("/{publicId}/reactivate")
    public ResponseEntity<Void> reactivateSeafarer(@PathVariable String publicId) {
        seafarerService.reactivateSeafarer(publicId);
        return ResponseEntity.noContent().build();
    }
}
