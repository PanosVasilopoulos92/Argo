package org.viators.argo.requisition;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition-lines")
@RequiredArgsConstructor
public class RequisitionLineController {

    private final RequisitionLineRepository requisitionLineRepository;

    public List<>
}
