package org.viators.argo.vessel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.DuplicateResourceException;
import org.viators.argo.vessel.dto.request.CreateVesselRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VesselService {

    private final VesselRepository vesselRepository;

    public String create(CreateVesselRequest request) {
        if (vesselRepository.existsByImoNumber(request.imoNumber())) {
            throw new DuplicateResourceException("Vessel", "imoNumber", request.imoNumber());
        }
        if (request.mmsiNumber() != null && vesselRepository.existsByMmsiNumber(request.mmsiNumber())) {
            throw new DuplicateResourceException("Vessel", "mmsiNumber", request.imoNumber());
        }

        VesselT vessel = request.toEntity();
        vessel = vesselRepository.save(vessel);

        return vessel.getPublicId();
    }
}
