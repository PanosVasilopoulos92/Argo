package org.viators.argo.requisition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.BusinessValidationException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.requisition.enums.RequisitionStateEnum;

import java.util.Objects;

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

        if (requisitionLine.getStatus().equals(ResourceStatusEnum.INACTIVE)) {
            throw new InvalidStateException("Requisition line with public Id: %s is inactive"
                .formatted(requisitionLine));
        }

        if (!Objects.equals(requisition.getRequisitionState(), RequisitionStateEnum.FINALIZED)) {
            throw new BusinessValidationException("Only lines that correspond to requisitions with state 'FINALIZED' can proceed to quotations." +
                "Line with publicId: %s belongs to a requisition with state '%s'"
                    .formatted(requisitionLine.getPublicId(), requisition.getRequisitionState().name())
            );
        }

        return requisitionLine;
    }
}
