package com.careerlink.dto.auth;

import com.careerlink.enums.Role;

import java.math.BigDecimal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @Size(max = 1000, message = "Skills must not exceed 1000 characters")
    private String skills;

    @Size(max = 255, message = "Resume must not exceed 255 characters")
    private String resume;

    @Size(max = 100, message = "Branch must not exceed 100 characters")
    private String branch;

    private Integer graduationYear;

    private BigDecimal cgpa;

    private Integer activeBacklogs;
}
