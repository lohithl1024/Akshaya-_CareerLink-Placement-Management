package com.careerlink.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.careerlink.dto.company.CompanyRequest;
import com.careerlink.dto.company.CompanyResponse;
import com.careerlink.entity.Company;
import com.careerlink.entity.User;
import com.careerlink.enums.Role;
import com.careerlink.exception.BadRequestException;
import com.careerlink.exception.ResourceNotFoundException;
import com.careerlink.repository.CompanyRepository;
import com.careerlink.repository.UserRepository;
import com.careerlink.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CompanyResponse createCompany(String email, CompanyRequest request) {
        String name = request.getName().trim();
        if (companyRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException("Company name already exists");
        }

        User currentUser = getUserByEmail(email);
        Company company = new Company();
        company.setName(name);
        company.setIndustry("General");
        company.setWebsite("https://example.com");
        company.setLocation("Not Specified");
        company.setDescription(request.getDescription().trim());
        company.setCreatedBy(currentUser);

        return mapCompany(companyRepository.save(company));
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapCompany)
                .toList();
    }

    @Override
    public List<CompanyResponse> getManagedCompanies(String email) {
        User currentUser = getUserByEmail(email);
        if (currentUser.getRole() == Role.ADMIN) {
            return getAllCompanies();
        }
        return companyRepository.findByCreatedByIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapCompany)
                .toList();
    }

    @Override
    public CompanyResponse getCompanyById(Long companyId) {
        return mapCompany(getCompanyEntity(companyId));
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long companyId, String email, CompanyRequest request) {
        Company company = getCompanyEntity(companyId);
        assertCanManageCompany(getUserByEmail(email), company);
        String name = request.getName().trim();

        if (companyRepository.existsByNameIgnoreCaseAndIdNot(name, companyId)) {
            throw new BadRequestException("Company name already exists");
        }

        company.setName(name);
        company.setDescription(request.getDescription().trim());
        return mapCompany(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void deleteCompany(Long companyId, String email) {
        Company company = getCompanyEntity(companyId);
        assertCanManageCompany(getUserByEmail(email), company);
        companyRepository.delete(company);
    }

    private Company getCompanyEntity(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertCanManageCompany(User currentUser, Company company) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (!company.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Recruiters can manage only their own company");
        }
    }

    private CompanyResponse mapCompany(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}
