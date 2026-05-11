package org.viators.argo.requisition.line;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.requisition.RequisitionT;
import org.viators.argo.requisition.enums.RequisitionStateEnum;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequisitionLineService {

    private final RequisitionLineRepository requisitionLineRepository;

    @Transactional(readOnly = true)
    public RequisitionLineT getLineAndValidateStatusAndStateForQuotation(String reqLinePublicId) {
        RequisitionLineT requisitionLine = requisitionLineRepository.findByPublicId(reqLinePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Requisition Line", "publicId", reqLinePublicId));

        RequisitionT requisition = requisitionLine.getRequisition();
        validateStatusAndStateForQuotation(requisitionLine, requisition);

        return requisitionLine;
    }

    @Transactional(readOnly = true)
    public List<RequisitionLineT> getLinesAndValidateForQuotation(Set<String> publicIds) {
        List<RequisitionLineT> reqLines = requisitionLineRepository.findByPublicIdIn(publicIds);

        validateLinesCorrespondToSameRequisition(reqLines);
        reqLines.forEach(requisitionLineT ->
            validateStatusAndStateForQuotation(requisitionLineT, requisitionLineT.getRequisition())
        );

        return reqLines;
    }

    @Transactional(readOnly = true)
    public RequisitionLineT getReqLine(String reqLinePublicId) {
        return requisitionLineRepository.findByPublicId(reqLinePublicId)
            .orElseThrow(() -> new ResourceNotFoundException("Requisition Line", "publicId", reqLinePublicId));
    }

    // Private helper methods
    private static void validateStatusAndStateForQuotation(RequisitionLineT requisitionLine, RequisitionT requisition) {
        if (requisitionLine.getStatus() == ResourceStatusEnum.INACTIVE) {
            throw new InvalidStateException("Requisition line with public Id: %s is inactive"
                .formatted(requisitionLine.getPublicId()));
        }

        if (requisition.getRequisitionState() != RequisitionStateEnum.FINALIZED) {
            throw new BusinessValidationException("Only lines that correspond to requisitions with state 'FINALIZED' can proceed." +
                "Line with publicId: %s belongs to a requisition with state '%s'"
                    .formatted(requisitionLine.getPublicId(), requisition.getRequisitionState().name())
            );
        }
    }

    private void validateLinesCorrespondToSameRequisition(List<RequisitionLineT> reqLines) {
        Set<String> requisitionPublicIds = reqLines.stream()
            .map(RequisitionLineT::getRequisition)
            .map(RequisitionT::getPublicId)
            .collect(Collectors.toSet());

        if (requisitionPublicIds.size() > 1) {
            throw new BusinessValidationException("Found requisition lines that correspond to different requisitions." +
                "Please check lines again and retry");
        }
    }
}
