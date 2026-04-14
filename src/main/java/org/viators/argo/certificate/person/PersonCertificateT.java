package org.viators.argo.certificate.person;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.certificate.CertificateT;
import org.viators.argo.person.PersonT;

@Entity
@Table(name = "person_certificates")
@DiscriminatorValue(value = "PERSON")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PersonCertificateT extends CertificateT {

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private PersonCertificateTypeEnum certificateType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonT person;
}
