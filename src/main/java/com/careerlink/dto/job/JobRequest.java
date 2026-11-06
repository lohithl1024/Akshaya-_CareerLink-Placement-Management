package com.careerlink.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 120, message = "Job title must not exceed 120 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 3000, message = "Description must not exceed 3000 characters")
    private String description;

    @NotNull(message = "Company id is required")
    private Long companyId;

    @NotBlank(message = "Eligibility is required")
    @Size(max = 1000, message = "Eligibility must not exceed 1000 characters")
    private String eligibility;

    @NotBlank(message = "Eligible branches are required")
    @Size(max = 500, message = "Eligible branches must not exceed 500 characters")
    private String eligibleBranches;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "0.00", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.00", message = "Minimum CGPA cannot exceed 10")
    private BigDecimal minimumCgpa;

    @NotNull(message = "Graduation year is required")
    @Min(value = 2020, message = "Graduation year must be valid")
    private Integer graduationYear;

    @NotNull(message = "Maximum active backlogs is required")
    @Min(value = 0, message = "Maximum active backlogs cannot be negative")
    private Integer maximumActiveBacklogs;
}
