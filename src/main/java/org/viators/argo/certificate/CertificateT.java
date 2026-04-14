package org.viators.argo.certificate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "certificates")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public abstract class CertificateT extends BaseEntity {

    @Column(name = "certificate_number", nullable = false, unique = true, length = 50)
    String certificateNumber;

    @Column(name = "issuing_authority", nullable = false, length = 100)
    String issuingAuthority;

    @Column(name = "issue_date", nullable = false)
    LocalDate issueDate;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "remarks", length = 500)
    String remarks;
}
