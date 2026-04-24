package org.viators.argo.requisition;

import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.requisition.enums.RequisitionPriorityEnum;
import org.viators.argo.requisition.enums.RequisitionStateEnum;
import org.viators.argo.requisition.enums.RequisitionTypeEnum;

import java.time.Instant;
import java.util.Set;

public class RequisitionSpecs {

    public static Specification<RequisitionT> hasRequisitionType(RequisitionTypeEnum requisitionType) {
        return (root, query, cb) ->
            cb.equal(root.get("requisitionType"), requisitionType);
    }

    public static Specification<RequisitionT> hasVesselPublicId(String vesselPublicId) {
        return (root, query, cb) ->
            cb.equal(root.get("targetVessel").get("publicId"), vesselPublicId);
    }

    public static Specification<RequisitionT> hasState(Set<RequisitionStateEnum> states) {
        return (root, query, cb) ->
            root.get("requisitionState").in(states);
    }

    public static Specification<RequisitionT> hasBeenRaisedBy(String personPublicId) {
        return (root, query, cb) ->
            cb.equal(root.get("raisedBy").get("publicId"), personPublicId);
    }

    public static Specification<RequisitionT> hasPriority(RequisitionPriorityEnum priority) {
        return (root, query, cb) ->
            cb.equal(root.get("requisitionPriority"), priority);
    }

    public static Specification<RequisitionT> hasCreatedDate(Instant dateFrom, Instant dateTo) {
        return (root, query, cb) -> {
            if (dateTo != null && dateFrom != null) {
                return cb.between(root.get("createdAt"), dateFrom, dateTo);
            }
            if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);
            }
            if (dateTo != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), dateTo);
            }
            return cb.conjunction();  // no filter — always true
        };
    }

    public static Specification<RequisitionT> hasRequiredDate(Instant dateFrom, Instant dateTo) {
        return (root, query, cb) -> {
            if (dateTo != null && dateFrom != null) {
                return cb.between(root.get("requiredByDate"), dateFrom, dateTo);
            }
            if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("requiredByDate"), dateFrom);
            }
            if (dateTo != null) {
                return cb.lessThanOrEqualTo(root.get("requiredByDate"), dateTo);
            }
            return cb.conjunction();
        };
    }

    public static Specification<RequisitionT> hasRequisitionNumber(String reqNumber) {
        return (root, query, cb) ->
            cb.equal(root.get("requisitionNumber"), reqNumber);
    }

}
