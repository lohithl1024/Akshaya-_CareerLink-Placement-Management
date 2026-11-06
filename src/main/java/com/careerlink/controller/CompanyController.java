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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careerlink.dto.common.ApiResponse;
import com.careerlink.dto.company.CompanyRequest;
import com.careerlink.dto.company.CompanyResponse;
import com.careerlink.service.CompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@AuthenticationPrincipal UserDetails userDetails,
                                                                      @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CompanyResponse>builder()
                        .success(true)
                        .message("Company created successfully")
                        .data(companyService.createCompany(userDetails.getUsername(), request))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        return ResponseEntity.ok(
                ApiResponse.<List<CompanyResponse>>builder()
                        .success(true)
                        .message("Companies fetched successfully")
                        .data(companyService.getAllCompanies())
                        .build()
        );
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getManagedCompanies(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.<List<CompanyResponse>>builder()
                        .success(true)
                        .message("Managed companies fetched successfully")
                        .data(companyService.getManagedCompanies(userDetails.getUsername()))
                        .build()
        );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable Long companyId) {
        return ResponseEntity.ok(
                ApiResponse.<CompanyResponse>builder()
                        .success(true)
                        .message("Company fetched successfully")
                        .data(companyService.getCompanyById(companyId))
                        .build()
        );
    }

    @PutMapping("/{companyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(@PathVariable Long companyId,
                                                                      @AuthenticationPrincipal UserDetails userDetails,
                                                                      @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<CompanyResponse>builder()
                        .success(true)
                        .message("Company updated successfully")
                        .data(companyService.updateCompany(companyId, userDetails.getUsername(), request))
                        .build()
        );
    }

    @DeleteMapping("/{companyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long companyId,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        companyService.deleteCompany(companyId, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Company deleted successfully")
                        .build()
        );
    }
}
