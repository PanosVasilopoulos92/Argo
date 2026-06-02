package org.viators.argo.docs.doccategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.viators.argo.docs.doccategory.dto.request.CreateDocCategoryRequest;
import org.viators.argo.docs.doccategory.dto.response.DocCategoryDetailsResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doc-categories")
@RequiredArgsConstructor
public class DocCategoryController {

    private final DocCategoryService docCategoryService;

    @PostMapping
    public ResponseEntity<DocCategoryDetailsResponse> create(
        @Valid @RequestBody CreateDocCategoryRequest request
    ) {
        DocCategoryDetailsResponse response = docCategoryService.create(request);
        return ResponseEntity
            .created(URI.create("/api/v1/doc-categories/" + response.publicId()))
            .body(response);
    }

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
    public ResponseEntity<DocCategoryDetailsResponse> getCategory(@PathVariable String docCategoryPublicId) {
        return ResponseEntity.ok(
            docCategoryService.getDocCategoryByPublicId(docCategoryPublicId)
        );
    }
}
