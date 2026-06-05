package org.viators.argo.docs.files;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.viators.argo.docs.files.dto.DocumentFileDownload;
import org.viators.argo.docs.files.dto.request.UploadDocumentFileRequest;
import org.viators.argo.docs.files.dto.response.DocumentFileDetailsResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;

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

    @GetMapping("/{storageKey}")
    public ResponseEntity<Resource> download(
        @PathVariable String storageKey,
        @RequestParam(name = "download", defaultValue = "false") boolean download
    ) {
        DocumentFileDownload file = documentFileService.download(storageKey);

        String disposition = download ? "attachment" : "inline";
        String headerValue = ContentDisposition.builder(disposition)
            .filename(file.originalFilename(), StandardCharsets.UTF_8)
            .build()
            .toString();

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.contentType()))
            .contentLength(file.fileSize())
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(file.resource());
    }
}
