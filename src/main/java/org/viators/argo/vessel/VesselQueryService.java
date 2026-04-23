package org.viators.argo.vessel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viators.argo.common.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class VesselQueryService {

    private final VesselRepository vesselRepository;

    @Transactional(readOnly = true)
    public VesselT getResourceByPublicId(String publicId) {
        return vesselRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "publicId", publicId));
    }

    @Transactional(readOnly = true)
    public VesselT getResourceByDatabaseId(Long vesselId) {
        return vesselRepository.findById(vesselId)
            .orElseThrow(() -> new ResourceNotFoundException("Vessel", "Id", vesselId));
    }
}
