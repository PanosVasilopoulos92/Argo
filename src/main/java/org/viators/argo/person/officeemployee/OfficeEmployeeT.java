package org.viators.argo.person.officeemployee;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.person.PersonT;

@Entity
@Table(name = "office_employees")
@DiscriminatorValue(value = "OFFICE_EMPLOYEE")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OfficeEmployeeT extends PersonT {

    private String testField;

}
