package org.viators.argo.vessel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.vessel.dto.request.CreateVesselRequest;
import org.viators.argo.vessel.dto.request.VesselFilterRequest;
import org.viators.argo.vessel.dto.response.VesselDetailsResponse;
import org.viators.argo.vessel.dto.response.VesselSummaryResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/vessels")
@RequiredArgsConstructor
public class VesselController {

    private final VesselService vesselService;

    @PreAuthorize("hasRole('FOM')")
    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateVesselRequest request) {

        String publicId = vesselService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/vessels/".concat(publicId)))
            .body(publicId);
    }

    @GetMapping("/{imoNumber}")
    public ResponseEntity<VesselDetailsResponse> getVesselByImoNumber(@PathVariable String imoNumber) {
        VesselDetailsResponse response = vesselService.getVesselByImoNumber(imoNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<VesselSummaryResponse>> getVesselsFiltered(
        @ModelAttribute VesselFilterRequest filter,
        @PageableDefault(sort = "vesselName", direction = Sort.Direction.ASC) Pageable pageable
        ) {
        Page<VesselSummaryResponse> response = vesselService.getVesselsFiltered(filter, pageable);
        return ResponseEntity.ok(response);
    }

}
