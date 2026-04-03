package org.viators.argo.person.onshoreemployee;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.person.PersonT;

@Entity
@Table(name = "onshore_employees")
@DiscriminatorValue(value = "ONSHORE_EMPLOYEE")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OnshoreEmployeeT extends PersonT {


}
