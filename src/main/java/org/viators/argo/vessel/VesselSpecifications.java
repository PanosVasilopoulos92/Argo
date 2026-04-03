package org.viators.argo.vessel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VesselSpecifications {

    public static Specification<VesselT> hasNameContaining(String name) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("vesselName")),
                "%".concat(name.toLowerCase()).concat("%")
            );
    }

    public static Specification<VesselT> hasVesselType(VesselTypeEnum vesselType) {
        return (root, query, cb) ->
            cb.equal(root.get("vesselType"), vesselType);
    }

    public static Specification<VesselT> hasFlagState(String flagState) {
        return (root, query, cb) ->
            cb.equal(root.get("flagState"), flagState.toUpperCase());
    }

    public static Specification<VesselT> hasActiveStatus(ResourceStatusEnum status) {
        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }

}
