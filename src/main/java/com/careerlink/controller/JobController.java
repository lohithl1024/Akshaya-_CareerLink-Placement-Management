package com.careerlink.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careerlink.dto.common.ApiResponse;
import com.careerlink.dto.job.JobRequest;
import com.careerlink.dto.job.JobResponse;
import com.careerlink.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@AuthenticationPrincipal UserDetails userDetails,
                                                              @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job created successfully")
                        .data(jobService.createJob(userDetails.getUsername(), request))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String eligibility,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(
                ApiResponse.<List<JobResponse>>builder()
                        .success(true)
                        .message("Jobs fetched successfully")
                        .data(jobService.getFilteredJobs(title, companyId, eligibility, active))
                        .build()
        );
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getManagedJobs(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<List<JobResponse>>builder()
                        .success(true)
                        .message("Managed jobs fetched successfully")
                        .data(jobService.getManagedJobs(userDetails.getUsername()))
                        .build()
        );
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long jobId) {
        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job fetched successfully")
                        .data(jobService.getJobById(jobId))
                        .build()
        );
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getJobsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(
                ApiResponse.<List<JobResponse>>builder()
                        .success(true)
                        .message("Jobs by company fetched successfully")
                        .data(jobService.getJobsByCompany(companyId))
                        .build()
        );
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(@PathVariable Long jobId,
                                                              @AuthenticationPrincipal UserDetails userDetails,
                                                              @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job updated successfully")
                        .data(jobService.updateJob(jobId, userDetails.getUsername(), request))
                        .build()
        );
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long jobId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        jobService.deleteJob(jobId, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Job deleted successfully")
                        .build()
        );
    }
}
