package org.viators.argo.certificate.vessel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.certificate.CertificateT;

@Entity
@Table(name = "vessel_certifications")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VesselCertificateT extends CertificateT {


}
