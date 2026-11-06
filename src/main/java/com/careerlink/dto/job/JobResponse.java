package com.careerlink.dto.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String eligibility;
    private String location;
    private String salaryPackage;
    private LocalDate applicationDeadline;
    private boolean active;
    private Long companyId;
    private String companyName;
    private String eligibleBranches;
    private BigDecimal minimumCgpa;
    private Integer graduationYear;
    private Integer maximumActiveBacklogs;
    private Boolean eligibleForCurrentStudent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
