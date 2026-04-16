package org.viators.argo.vessel;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.assignment.AssignmentT;
import org.viators.argo.certificate.vessel.VesselCertificateT;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.vessel.enums.ClassificationSocietyEnum;
import org.viators.argo.vessel.enums.VesselTypeEnum;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vessels")
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class VesselT extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String vesselName;

    @Column(name = "imo_number", nullable = false, unique = true, updatable = false)
    private String imoNumber; // International Maritime Organization ID

    @Column(name = "mmsi_number", unique = true)
    private String mmsiNumber; // Maritime Mobile Service Identity

    @Column(name = "call_sign", unique = true, length = 10)
    private String callSign;

    @Column(name = "flag_state", nullable = false, length = 3)
    private String flagState; //  Always in ISO 3166-1 alpha-3

    @Enumerated(EnumType.STRING)
    @Column(name = "vessel_type", nullable = false)
    private VesselTypeEnum vesselType;

    @Column(name = "gross_tonnage")
    private Double grossTonnage;

    @Column(name = "net_tonnage")
    private Double netTonnage;

    @Column(name = "dead_weight_tonnage")
    private Double deadWeightTonnage;

    @Column(name = "year_build")
    private Integer yearBuild;

    @Column(name = "builder", length = 100)
    private String builder;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification_society", length = 100)
    private ClassificationSocietyEnum classificationSociety;

    @Column(name = "port_of_registry", length = 100)
    private String portOfRegistry;

    @OneToMany(mappedBy = "vessel", fetch = FetchType.LAZY)
    private Set<VesselCertificateT> certificates = new HashSet<>();

    @OneToMany(mappedBy = "vessel", fetch = FetchType.LAZY)
    private Set<AssignmentT> assignments = new HashSet<>();

    // Helper methods
    public void addCertificate(VesselCertificateT certificate) {
        if (!certificates.contains(certificate)) {
            certificates.add(certificate);
            certificate.setVessel(this);
        }
    }
}
