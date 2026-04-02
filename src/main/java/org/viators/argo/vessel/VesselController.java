package org.viators.argo.vessel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.vessel.dto.request.CreateVesselRequest;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/vessels")
@RequiredArgsConstructor
public class VesselController {

    private final VesselService vesselService;

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateVesselRequest request) {

        String publicId = vesselService.create(request);

        return ResponseEntity
            .created(URI.create("/api/v1/vessels/".concat(publicId)))
            .body(publicId);
    }

}
