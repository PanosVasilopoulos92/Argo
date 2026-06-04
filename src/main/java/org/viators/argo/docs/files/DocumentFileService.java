package org.viators.argo.docs.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.common.validation.PdfValidator;
import org.viators.argo.docs.doccategory.DocCategoryService;
import org.viators.argo.docs.doccategory.DocCategoryT;
import org.viators.argo.docs.files.dto.request.UploadDocumentFileRequest;
import org.viators.argo.docs.files.dto.response.DocumentFileDetailsResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentFileService {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024; // 50 MB

    private final DocumentFileRepository documentFileRepository;
    private final DocCategoryService docCategoryService;
    private final DocumentFileStorageService documentFileStorageService;
    private final PdfValidator pdfValidator;

    public DocumentFileDetailsResponse upload(UploadDocumentFileRequest request, MultipartFile file) {
        pdfValidator.validate(file);

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessValidationException(
                "File exceeds the 50 MB limit. Actual size: " + file.getSize() + " bytes"
            );
        }

        DocCategoryT docCategory = docCategoryService.getDocCategoryResourceByPublicId(request.docCategoryPublicId());
        String storageKey = UUID.randomUUID().toString();

        documentFileStorageService.store(file, storageKey);

        //    Register a rollback compensation. If anything past this point
        //    causes the transaction to roll back — a DB constraint
        //    violation, a RuntimeException from a downstream call, an
        //    optimistic-lock failure on commit — Spring will call our
        //    afterCompletion with 'STATUS_ROLLED_BACK' and we delete the
        //    orphan file
        registerFileRollbackHook(storageKey);

        DocumentFileT documentFile = DocumentFileT.builder()
            .name(request.name())
            .description(request.description())
            .originalFilename(file.getOriginalFilename())
            .contentType(file.getContentType())
            .fileSize(file.getSize())
            .storageKey(storageKey)
            .build();

        docCategory.addDocumentFile(documentFile);

        documentFile = documentFileRepository.save(documentFile);

        log.info("Uploaded document file publicId={}, storageKey={}, category={}",
            documentFile.getPublicId(), storageKey, docCategory.getName());

        return DocumentFileDetailsResponse.from(documentFile);

    }

    private void registerFileRollbackHook(String storageKey) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        log.warn("Transaction rolled back; deleting orphan file {}", storageKey);
                        documentFileStorageService.delete(storageKey);
                    }
                }
            }
        );
    }
}
