package org.viators.argo.docs.doccategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.docs.doccategory.dto.request.CreateDocCategoryRequest;
import org.viators.argo.docs.doccategory.dto.request.UpdateDocCategoryRequest;
import org.viators.argo.docs.doccategory.dto.response.DocCategoryDetailsResponse;
import org.viators.argo.docs.doccategory.dto.response.DocCategoryWithDocumentsResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doc-categories")
@RequiredArgsConstructor
public class DocCategoryController {

    private final DocCategoryService docCategoryService;

    @PreAuthorize("hasRole('DOC_ADMIN')")
    @PostMapping
    public ResponseEntity<DocCategoryDetailsResponse> create(
        @Valid @RequestBody CreateDocCategoryRequest request
    ) {
        DocCategoryDetailsResponse response = docCategoryService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/doc-categories/" + response.publicId()))
            .body(response);
    }

    @PatchMapping("/{docCategoryPublicId}/update")
    public ResponseEntity<DocCategoryDetailsResponse> update(
        @PathVariable String docCategoryPublicId,
        @Valid @RequestBody UpdateDocCategoryRequest request
    ) {
        return ResponseEntity.ok(
            docCategoryService.update(docCategoryPublicId, request)
        );
    }

    @PreAuthorize("hasRole('DOC_ADMIN')")
    @DeleteMapping("/{docCategoryPublicId}")
    public ResponseEntity<Void> delete(@PathVariable String docCategoryPublicId) {
        docCategoryService.delete(docCategoryPublicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DocCategoryDetailsResponse>> getAllDocCategories() {
        return ResponseEntity.ok(
            docCategoryService.getAllDocCategories()
        );
    }

    @GetMapping("/{docCategoryPublicId}")
    public ResponseEntity<DocCategoryDetailsResponse> getCategory(
        @PathVariable String docCategoryPublicId
    ) {
        return ResponseEntity.ok(
            docCategoryService.getDocCategoryByPublicId(docCategoryPublicId)
        );
    }

    @GetMapping("/{docCategoryPublicId}/files-summary")
    public ResponseEntity<DocCategoryWithDocumentsResponse> getCategoryWithDocFilesSummary(
        @PathVariable String docCategoryPublicId
    ) {
        return ResponseEntity.ok(
            docCategoryService.getDocCategoryWithDocFiles(docCategoryPublicId)
        );
    }
}
