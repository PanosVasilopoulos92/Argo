package org.viators.argo.docs.files;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.viators.argo.docs.files.dto.request.UploadDocumentFileRequest;
import org.viators.argo.docs.files.dto.response.DocumentFileDetailsResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/doc-files")
@RequiredArgsConstructor
public class DocumentFileController {

    private final DocumentFileService documentFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DOC_ADMIN')")
    public ResponseEntity<DocumentFileDetailsResponse> upload(
        @Valid @RequestPart("metadata") UploadDocumentFileRequest metadata,
        @RequestPart("file") MultipartFile file
    ) {
        DocumentFileDetailsResponse response = documentFileService.upload(metadata, file);
        return ResponseEntity
            .created(URI.create("/api/v1/document-files/" + response.publicId()))
            .body(response);
    }
}
