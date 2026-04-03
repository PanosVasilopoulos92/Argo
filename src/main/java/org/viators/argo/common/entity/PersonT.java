package org.viators.argo.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.BaseEntity;
import org.viators.argo.common.enums.GenderEnum;

import java.time.LocalDate;

@Entity
@Table(name = "persons")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PersonT extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @Column(name = "father_name", length = 30)
    private String fatherName;

    @Column(name = "mother_name", length = 30)
    private String motherName;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "birth_place", length = 20)
    private String birthPlace;

    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "passport_expiry_date")
    private LocalDate passportExpiryDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "passport_issued")
    private LocalDate passportIssued;

    @Column(name = "sb_issued")
    private LocalDate sbIssued;

    @Column(name = "sb_expiry")
    private LocalDate sbExpiry;

    @Column(name = "bank_name", length = 60)
    private String bankName;

    @Column(name = "bank_account", length = 20)
    private String bankAccount;
}
