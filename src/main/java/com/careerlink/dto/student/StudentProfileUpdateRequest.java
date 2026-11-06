package com.careerlink.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileUpdateRequest {

    @NotBlank(message = "Skills are required")
    @Size(max = 1000, message = "Skills must not exceed 1000 characters")
    private String skills;

    @NotBlank(message = "Resume is required")
    @Size(max = 255, message = "Resume must not exceed 255 characters")
    private String resume;

    @NotBlank(message = "Branch is required")
    @Size(max = 100, message = "Branch must not exceed 100 characters")
    private String branch;

    @NotNull(message = "Graduation year is required")
    @Min(value = 2020, message = "Graduation year must be valid")
    private Integer graduationYear;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.00", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.00", message = "CGPA cannot exceed 10")
    private BigDecimal cgpa;

    @NotNull(message = "Active backlog count is required")
    @Min(value = 0, message = "Active backlogs cannot be negative")
    private Integer activeBacklogs;
}
