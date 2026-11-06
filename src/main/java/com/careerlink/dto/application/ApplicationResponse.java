package com.careerlink.dto.application;

import java.time.LocalDateTime;

import com.careerlink.enums.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Boolean meetsEligibility;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
