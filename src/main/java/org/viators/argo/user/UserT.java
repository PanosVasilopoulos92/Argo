package org.viators.argo.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.viators.argo.common.entity.BaseEntity;
import org.viators.argo.person.PersonT;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserT extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // Keycloak's UUID for this user — the "sub" claim in JWTs.
    // Use this to look up the local user record from an incoming JWT.
    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoleEnum userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private UserLevelEnum level;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "id", updatable = false)
    private PersonT person;
}
