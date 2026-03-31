package org.viators.argo.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.BaseEntity;

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

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "father_name", length = 30)
    private String fatherName;

    @Column(name = "mother_name", length = 30)
    private String motherName;

    @Column(name = "nationality", length = 3)
    private String nationality;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "born_place", length = 20)
    private String bornPlace;

    @Column(name = "sex", length = 1)
    private String sex;

    @Column(name = "passport_number", length = 15)
    private String passportNumber;

    @Column(name = "passport_expiry_date")
    private LocalDate passportExpiryDate;

    @Column(name = "rank", length = 3)
    private String rank;

    @Column(name = "rank_certificate", length = 5)
    private String rankCertificate;

    @Column(name = "seaman_book", length = 15)
    private String seamanBook;

    @Column(name = "prior_exp_months")
    private Double priorExpMonths;

    @Column(name = "prior_exp_voyages")
    private Double priorExpVoyages;

    @Column(name = "last_sign_off_date")
    private LocalDate lastSignOffDate;

    @Column(name = "available_from_date")
    private LocalDate availableFromDate;

    @Column(name = "decision_code", length = 4)
    private String decisionCode;

    @Column(name = "active_flag", length = 1)
    private String activeFlag;

    @Column(name = "remarks_1")
    private String remarks1;

    @Column(name = "remarks_2")
    private String remarks2;

    @Column(name = "health_status", length = 1)
    private String healthStatus;

    @Column(name = "source_code", length = 3)
    private String sourceCode;

    @Column(name = "passport_issued")
    private LocalDate passportIssued;

    @Column(name = "sb_issued")
    private LocalDate sbIssued;

    @Column(name = "sb_expiry")
    private LocalDate sbExpiry;

    @Column(name = "name_2", length = 30)
    private String personsName2;

    @Column(name = "nationality_2", length = 3)
    private String personsNationality2;

    @Column(name = "next_vessel", length = 5)
    private String nextVessel;

    @Column(name = "promotion", length = 1)
    private String promotion;

    @Column(name = "seniority")
    private Integer seniority;

    @Column(name = "promotion_date")
    private LocalDate promotionDate;

    @Column(name = "promotion_rank", length = 3)
    private String promotionRank;

    @Column(name = "date_last_med")
    private LocalDate dateLastMed;

    @Column(name = "prev_exp_as", length = 3)
    private String prevExpAs;

    @Column(name = "crew_type", length = 5)
    private String crewType;

    @Column(name = "religion", length = 5)
    private String religion;

    @Column(name = "manual_update", length = 1)
    private String manualUpdate;

    @Column(name = "pension_flag", length = 1)
    private String pensionFlag;

    @Column(name = "est_sign_on")
    private LocalDate estSignOn;

    @Column(name = "sign_on_remarks", length = 60)
    private String signOnRemarks;

    @Column(name = "sign_on_remarks_2", length = 60)
    private String signOnRemarks2;

    @Column(name = "sign_on_remarks_3", length = 60)
    private String signOnRemarks3;

    @Column(name = "sb_place", length = 30)
    private String sbPlace;

    @Column(name = "port_authority", length = 30)
    private String portAuthority;

    @Column(name = "us_visa_flag", length = 1)
    private String usVisaFlag;

    @Column(name = "us_visa_expiry")
    private LocalDate usVisaExpiry;

    @Column(name = "port_authority_2", length = 30)
    private String portAuthority2;

    @Column(name = "rank_seniority")
    private Integer rankSeniority;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "photo", length = 30)
    private String photo;

    @Column(name = "passport_details", length = 30)
    private String passportDetails;

    @Column(name = "sb_details", length = 30)
    private String sbDetails;

    @Column(name = "ssn", length = 15)
    private String personsSsn;

    @Column(name = "isn_employment_no", length = 15)
    private String isnEmploymentNo;

    @Column(name = "employment_year")
    private LocalDate employmentYear;

    @Column(name = "bank_name", length = 60)
    private String bankName;

    @Column(name = "bank_account", length = 20)
    private String bankAccount;
}
