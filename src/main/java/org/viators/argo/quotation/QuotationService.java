package org.viators.argo.quotation;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.quotation.dto.request.*;
import org.viators.argo.quotation.dto.response.QuotationDetailsResponse;
import org.viators.argo.quotation.dto.response.QuotationSummaryResponse;
import org.viators.argo.quotation.enums.QuotationStateEnum;
import org.viators.argo.requisition.line.RequisitionLineService;
import org.viators.argo.requisition.line.RequisitionLineT;
import org.viators.argo.supplier.SupplierService;
import org.viators.argo.supplier.SupplierT;
import org.viators.argo.user.UserService;
import org.viators.argo.user.UserT;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final RequisitionLineService requisitionLineService;
    private final SupplierService supplierService;
    private final UserService userService;

    @Transactional
    public QuotationDetailsResponse create(CreateQuotationRequest request) {
        RequisitionLineT requisitionLine = requisitionLineService.getLineAndValidateStatusAndStateForQuotation(
            request.requisitionLinePublicId());
        SupplierT supplier = supplierService.getActiveResource(request.supplierPublicId());

        QuotationT quotation = request.toEntity(requisitionLine, supplier);
        return QuotationDetailsResponse.from(quotationRepository.save(quotation));
    }

    @Transactional
    public List<QuotationDetailsResponse> createBulk(BulkCreateQuotationsRequest request) {
        SupplierT supplier = supplierService.getActiveResource(request.supplierPublicId());
        Set<String> reqLinesIds = validateLines(request.lineQuotations());

        Map<String, RequisitionLineT> linesByPublicId = requisitionLineService
            .getLinesAndValidateForQuotation(reqLinesIds)
            .stream()
            .collect(Collectors.toMap(RequisitionLineT::getPublicId, line -> line));

        return request.lineQuotations().stream()
            .map(lineQuotation -> {
                RequisitionLineT requisitionLine = linesByPublicId.get(lineQuotation.requisitionLinePublicId());
                QuotationT quotation = request.toEntity(supplier, lineQuotation);
                quotation.setReqLine(requisitionLine);
                return quotationRepository.save(quotation);
            })
            .map(QuotationDetailsResponse::from)
            .toList();
    }

    @Transactional
    public void acceptQuotation(String keycloakId, String quotPublicId, AcceptQuotationRequest request) {
        QuotationT quotation = loadSourceAndValidateStatusAndVersion(quotPublicId, request.version());
        UserT loggedInUser = userService.getUser(keycloakId);
        validateQuotationStateForAcceptance(quotation);

        quotation.setAcceptedAt(Instant.now());
        quotation.setAcceptedBy(loggedInUser.getUsername());
        quotation.setQuotationState(QuotationStateEnum.ACCEPTED);
    }

    @Transactional
    public void rejectQuotation(String keycloakId, String quotPublicId, RejectQuotationRequest request) {
        QuotationT quotation = loadSourceAndValidateStatusAndVersion(quotPublicId, request.version());
        UserT loggedInUser = userService.getUser(keycloakId);

        if (!QuotationStateEnum.RECEIVED.equals(quotation.getQuotationState())) {
            throw new InvalidStateException("Quotation with publicId: %s is in state '%s'. Only quotations in state '%s' can be rejected."
                .formatted(quotation.getPublicId(), quotation.getQuotationState(), QuotationStateEnum.RECEIVED.name()));
        }

        quotation.setRejectedAt(Instant.now());
        quotation.setRejectedBy(loggedInUser.getUsername());
        quotation.setRejectionReason(request.rejectionReason());
    }

    // Read only methods
    @Transactional(readOnly = true)
    public QuotationDetailsResponse getQuotation(String publicId) {
        QuotationT quotation = quotationRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Quotation", "publicId", publicId));

        return QuotationDetailsResponse.from(quotation);
    }

    @Transactional(readOnly = true)
    public Page<QuotationSummaryResponse> getQuotations(Pageable pageable) {
        return quotationRepository.findAllForSummary(pageable)
            .map(QuotationSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Set<QuotationT> getQuotationsForPO(Set<String> quotationPublicIds) {
        return quotationRepository.findQuotationsForPO(quotationPublicIds);
    }

    @Transactional(readOnly = true)
    public List<QuotationSummaryResponse> getQuotationsForReqLine(String reqLinePublicId) {
        return quotationRepository.findAllQuotationsForReqLine(reqLinePublicId).stream()
            .map(QuotationSummaryResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<QuotationSummaryResponse> getQuotationsFiltered(SearchQuotationFilteredRequest request,
                                                                Pageable pageable
    ) {
        Specification<QuotationT> specs = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(request.quotationPublicId())) {
            specs = specs.and(QuotationSpecs.hasPublicId(request.quotationPublicId()));
        }

        if (StringUtils.hasText(request.requisitionPublicId())) {
            specs = specs.and(QuotationSpecs.hasRequisitionPublicId(request.requisitionPublicId()));
        }

        if (request.quotationState() != null) {
            specs = specs.and(QuotationSpecs.hasState(request.quotationState()));
        }

        if (request.excludeExpired()) {
            specs = specs.and(QuotationSpecs.hasNotExpired());
        }

        specs = specs.and(QuotationSpecs.hasValidDateRange(request.validUntilFrom(), request.validUntilTo()));

        return quotationRepository.findAll(specs, pageable)
            .map(QuotationSummaryResponse::from);
    }

    // Private helper methods
    private Set<String> validateLines(List<BulkCreateQuotationsRequest.LineQuotation> lineQuotations) {
        List<String> lineIds = lineQuotations.stream()
            .map(BulkCreateQuotationsRequest.LineQuotation::requisitionLinePublicId)
            .toList();

        Set<String> uniqueLineIds = lineQuotations.stream()
            .map(BulkCreateQuotationsRequest.LineQuotation::requisitionLinePublicId)
            .collect(Collectors.toSet());

        if (lineIds.size() != uniqueLineIds.size()) {
            throw new BusinessValidationException("You provided quotation request more than once for the same requisition line." +
                " Fix it and try again"
            );
        }

        return uniqueLineIds;
    }

    private QuotationT loadSourceAndValidateStatusAndVersion(String quotPublicId, Long requestVersion) {
        QuotationT quotation = quotationRepository.findByPublicId(quotPublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Quotation", "publicId", quotPublicId));

        if (ResourceStatusEnum.INACTIVE.equals(quotation.getStatus())) {
            throw new InvalidStateException("Quotation with publicId: %s is inactive. Action cannot proceed"
                .formatted(quotPublicId));
        }

        if (!Objects.equals(quotation.getVersion(), requestVersion)) {
            throw new OptimisticLockException("Another user has concurrently modified the same resource. Please try again");
        }

        return quotation;
    }

    private void validateQuotationStateForAcceptance(QuotationT quotation) {
        if (!QuotationStateEnum.RECEIVED.equals(quotation.getQuotationState())) {
            throw new InvalidStateException("Quotation with publicId: %s is in state '%s'. Only quotations in state '%s' can be accepted."
                .formatted(quotation.getPublicId(), quotation.getQuotationState(), QuotationStateEnum.RECEIVED.name()));
        }

        if (quotation.getSupplier().getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("Quotation cannot proceed because supplier(publicId: %s) is inactive"
                .formatted(quotation.getSupplier().getPublicId()));
        }

        if (quotation.getValidUntil().isBefore(LocalDate.now())) {
            throw new BusinessValidationException(("Quotation cannot proceed because is not valid any more." +
                " Expired at %s so a request re-quote or reject it.")
                .formatted(quotation.getValidUntil().toString()));
        }
    }
}
