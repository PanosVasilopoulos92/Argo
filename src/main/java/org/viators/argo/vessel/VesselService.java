package org.viators.argo.vessel;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.viators.argo.common.enums.ResourceStatusEnum;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.common.exceptions.ResourceNotFoundException;
import org.viators.argo.vessel.dto.request.CreateVesselRequest;
import org.viators.argo.vessel.dto.request.UpdateVesselInfoRequest;
import org.viators.argo.vessel.dto.request.VesselFilterRequest;
import org.viators.argo.vessel.dto.response.VesselDetailsResponse;
import org.viators.argo.vessel.dto.response.VesselSummaryResponse;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VesselService {

    private final VesselRepository vesselRepository;

    public String create(CreateVesselRequest request) {

        operationIsValid(request.vesselName(), request.mmsiNumber(), request.callSign(), request.imoNumber());

        VesselT vessel = request.toEntity();
        vessel = vesselRepository.save(vessel);

        return vessel.getPublicId();
    }

    public VesselDetailsResponse updateVesselInfo(String publicId, UpdateVesselInfoRequest request) {

        VesselT vessel = vesselRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "publicId", publicId));

        if (!Objects.equals(request.version(), vessel.getVersion())) {
            throw new OptimisticLockException("Another user has modified same vessel before you");
        }

        operationIsValid(request.vesselName(), request.mmsiNumber(), request.callSign(), null);

        request.update(vessel); // Dirty checking update
        return VesselDetailsResponse.from(vessel);
    }

    public void deactivateVessel(String publicId) {
        VesselT vessel = vesselRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "publicId", publicId));

        if (!ResourceStatusEnum.ACTIVE.equals(vessel.getStatus())) {
            throw new InvalidStateException("Vessel with publicId: %s is already deactivated".formatted(publicId));
        }

        vessel.setStatus(ResourceStatusEnum.INACTIVE);
    }

    public void reactivateVessel(String publicId) {
        VesselT vessel = vesselRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "publicId", publicId));

        if (ResourceStatusEnum.ACTIVE.equals(vessel.getStatus())) {
            throw new InvalidStateException("Vessel with publicId: %s is already active".formatted(publicId));
        }

        vessel.setStatus(ResourceStatusEnum.ACTIVE);
    }

    @Transactional(readOnly = true)
    public VesselDetailsResponse getVesselByImoNumber(String imoNumber) {

        VesselT vessel = vesselRepository.findByImoNumber(imoNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "imoNumber", imoNumber));

        return VesselDetailsResponse.from(vessel);
    }

    @Transactional(readOnly = true)
    public VesselDetailsResponse getVesselByPublicId(String publicId) {

        VesselT vessel = vesselRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "publicId", publicId));

        return VesselDetailsResponse.from(vessel);
    }

    @Transactional(readOnly = true)
    public Page<VesselSummaryResponse> getVesselsFiltered(VesselFilterRequest filter, Pageable pageable) {

        // Produces a predicate that's always true in order to initialize specs
        Specification<VesselT> specs = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(filter.vesselNameContaining())) {
            specs = specs.and(VesselSpecifications.hasNameContaining(filter.vesselNameContaining()));
        }

        if (StringUtils.hasText(filter.flagState())) {
            specs = specs.and(VesselSpecifications.hasFlagState(filter.flagState()));
        }

        if (filter.vesselType() != null) {
            specs = specs.and(VesselSpecifications.hasVesselType(filter.vesselType()));
        }

        if (!filter.includeInactiveVessels()) {
            specs = specs.and(VesselSpecifications.hasActiveStatus(ResourceStatusEnum.ACTIVE));
        }

        return vesselRepository.findAll(specs, pageable)
            .map(VesselSummaryResponse::from);
    }

    // Helper private methods
    private void operationIsValid(String vesselName, String mmsiNumber, String callSign, String imoNumber) {

        if (StringUtils.hasText(vesselName) && vesselRepository.existsByVesselName(vesselName)) {
            throw new DuplicateResourceException("Vessel", "vesselName", vesselName);
        }

        if (StringUtils.hasText(mmsiNumber) && vesselRepository.existsByMmsiNumber(mmsiNumber)) {
            throw new DuplicateResourceException("Vessel", "mmsiNumber", mmsiNumber);
        }

        if (StringUtils.hasText(callSign) && vesselRepository.existsByCallSign(callSign)) {
            throw new DuplicateResourceException("Vessel", "callSign", callSign);
        }

        if (StringUtils.hasText(imoNumber) && vesselRepository.existsByImoNumber(imoNumber)) {
            throw new DuplicateResourceException("Vessel", "imoNumber", imoNumber);
        }
    }

}
