package org.viators.argo.certificate.vessel;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.certificate.CertificateT;
import org.viators.argo.certificate.vessel.enums.VesselCertificateTypeEnum;
import org.viators.argo.vessel.VesselT;

@Entity
@Table(name = "vessel_certifications")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VesselCertificateT extends CertificateT {

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private VesselCertificateTypeEnum certificateType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vessel_id", nullable = false)
    private VesselT vessel;

}
