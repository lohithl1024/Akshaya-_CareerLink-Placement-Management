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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careerlink.dto.application.ApplicationRequest;
import com.careerlink.dto.application.ApplicationResponse;
import com.careerlink.dto.application.ApplicationStatusUpdateRequest;
import com.careerlink.dto.common.ApiResponse;
import com.careerlink.service.ApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application created successfully")
                        .data(applicationService.createApplication(userDetails.getUsername(), request))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAllApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Applications fetched successfully")
                        .data(applicationService.getManagedApplications(userDetails.getUsername()))
                        .build()
        );
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getManagedApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Managed applications fetched successfully")
                        .data(applicationService.getManagedApplications(userDetails.getUsername()))
                        .build()
        );
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(@PathVariable Long applicationId,
                                                                                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application fetched successfully")
                        .data(applicationService.getApplicationById(applicationId, userDetails.getUsername()))
                        .build()
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Student applications fetched successfully")
                        .data(applicationService.getMyApplications(userDetails.getUsername()))
                        .build()
        );
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Applications by student fetched successfully")
                        .data(applicationService.getApplicationsByStudent(studentId))
                        .build()
        );
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsByJob(@PathVariable Long jobId,
                                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Applications by job fetched successfully")
                        .data(applicationService.getApplicationsByJob(jobId, userDetails.getUsername()))
                        .build()
        );
    }

    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','RECRUITER')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application status updated successfully")
                        .data(applicationService.updateApplicationStatus(applicationId, userDetails.getUsername(), request))
                        .build()
        );
    }

    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Application deleted successfully")
                        .build()
        );
    }
}
