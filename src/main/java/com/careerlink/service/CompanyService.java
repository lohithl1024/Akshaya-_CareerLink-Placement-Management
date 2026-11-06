package com.careerlink.service;

import java.util.List;

import com.careerlink.dto.company.CompanyRequest;
import com.careerlink.dto.company.CompanyResponse;

public interface CompanyService {

    CompanyResponse createCompany(String email, CompanyRequest request);

    List<CompanyResponse> getAllCompanies();

    List<CompanyResponse> getManagedCompanies(String email);

    CompanyResponse getCompanyById(Long companyId);

    CompanyResponse updateCompany(Long companyId, String email, CompanyRequest request);

    void deleteCompany(Long companyId, String email);
}
