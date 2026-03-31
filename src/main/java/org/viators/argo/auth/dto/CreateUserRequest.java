package org.viators.argo.auth.dto;

import jakarta.validation.constraints.*;
import org.viators.argo.user.UserRoleEnum;

import java.time.LocalDate;

public record CreateUserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @Past(message = "Date of birth cannot be in future date")
    LocalDate dateOfBirth,

    @NotNull
    UserRoleEnum userRole
) {
}
