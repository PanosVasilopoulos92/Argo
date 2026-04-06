package org.viators.argo.person.seafarer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.viators.argo.person.seafarer.dto.request.CreateSeafarerRequest;

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
}
