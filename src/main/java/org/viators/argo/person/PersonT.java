package org.viators.argo.person;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.common.enums.GenderEnum;

import java.time.LocalDate;

@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "person_type")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public abstract class PersonT extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @Column(name = "father_name", nullable = false, length = 30)
    private String fatherName;

    @Column(name = "mother_name", length = 30)
    private String motherName;

    @Column(name = "nationality", nullable = false, length = 3)
    private String nationality;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "birth_place", length = 20)
    private String birthPlace;

    @Column(name = "gender", nullable = false)
    private GenderEnum gender;

    @Column(name = "passport_number", unique = true)
    private String passportNumber;

    @Column(name = "passport_expiry_date")
    private LocalDate passportExpiryDate;

    @Column(name = "passport_issued")
    private LocalDate passportIssuedDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "bank_name", length = 60)
    private String bankName;

    @Column(name = "bank_account", length = 20)
    private String bankAccount;
}
