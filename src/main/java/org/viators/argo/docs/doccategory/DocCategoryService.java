package org.viators.argo.docs.doccategory;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.docs.doccategory.dto.request.CreateDocCategoryRequest;
import org.viators.argo.docs.doccategory.dto.request.UpdateDocCategoryRequest;
import org.viators.argo.docs.doccategory.dto.response.DocCategoryDetailsResponse;
import org.viators.argo.docs.doccategory.dto.response.DocCategoryWithDocumentsResponse;
import org.viators.argo.docs.files.dto.response.DocumentFileSummaryResponse;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocCategoryService {

    private final DocCategoryRepository docCategoryRepository;

    @Transactional
    public DocCategoryDetailsResponse create(CreateDocCategoryRequest request) {
        DocCategoryT docCategory = request.toEntity();

        if (docCategoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("There is already a doc category with this name");
        }

        return DocCategoryDetailsResponse.from(
            docCategoryRepository.save(docCategory)
        );
    }

    @Transactional
    public DocCategoryDetailsResponse update(String docCategoryPublicId, UpdateDocCategoryRequest request) {
        DocCategoryT docCategory = loadResourceAndCheckVersion(docCategoryPublicId, request.getVersion());

        request.getName().ifPresent(name -> {
            if (docCategoryRepository.existsByName(name)) {
                throw new DuplicateResourceException("There is already a doc category with this name");
            }
        });

        request.update(docCategory);

        return DocCategoryDetailsResponse.from(docCategoryRepository.save(docCategory));
    }

    @Transactional
    public void delete(String docCategoryPublicId) {
        DocCategoryT docCategory = docCategoryRepository.findByPublicId(docCategoryPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("DocCategory", "publicId", docCategoryPublicId));

        if (docCategoryRepository.existsByIdAndDocumentFilesNotEmpty(docCategory.getId())) {
            throw new InvalidStateException("Doc category with publicId: %s contains files and cannot be deleted."
                .formatted(docCategoryPublicId));
        }

        docCategoryRepository.delete(docCategory);
    }

    // Read only methods
    @Transactional(readOnly = true)
    public List<DocCategoryDetailsResponse> getAllDocCategories() {
        return docCategoryRepository.findAll().stream()
            .map(DocCategoryDetailsResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public DocCategoryDetailsResponse getDocCategoryByPublicId(String docCategoryPublicId) {
        DocCategoryT docCategory = docCategoryRepository.findByPublicId(docCategoryPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("DocCategory", "publicId", docCategoryPublicId));

        return DocCategoryDetailsResponse.from(docCategory);
    }

    @Transactional(readOnly = true)
    public DocCategoryT getDocCategoryResourceByPublicId(String docCategoryPublicId) {
        return docCategoryRepository.findByPublicId(docCategoryPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("DocCategory", "publicId", docCategoryPublicId));
    }

    @Transactional(readOnly = true)
    public DocCategoryWithDocumentsResponse getDocCategoryWithDocFiles(String docCategoryPublicId) {
        DocCategoryT result = docCategoryRepository.findByPublicIdWithDocFiles(docCategoryPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("DocCategory", "publicId", docCategoryPublicId));

        List<DocumentFileSummaryResponse> documentFileSummaryResponse = result.getDocumentFiles().stream()
            .map(DocumentFileSummaryResponse::from)
            .toList();

        return DocCategoryWithDocumentsResponse.from(result, documentFileSummaryResponse);
    }

    // Private helper methods
    private DocCategoryT loadResourceAndCheckVersion(String docCategoryPublicId, Long providedVersion) {
        DocCategoryT docCategory = docCategoryRepository.findByPublicId(docCategoryPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("DocCategory", "publicId", docCategoryPublicId));

        if (!Objects.equals(docCategory.getVersion(), providedVersion)) {
            throw new OptimisticLockException("Another user has concurrently updated resource. Please try again");
        }

        return docCategory;
    }

}
