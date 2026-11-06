package com.careerlink.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.careerlink.dto.job.JobRequest;
import com.careerlink.dto.job.JobResponse;
import com.careerlink.entity.Company;
import com.careerlink.entity.Job;
import com.careerlink.entity.User;
import com.careerlink.entity.Student;
import com.careerlink.enums.Role;
import com.careerlink.exception.ResourceNotFoundException;
import com.careerlink.repository.CompanyRepository;
import com.careerlink.repository.JobRepository;
import com.careerlink.repository.UserRepository;
import com.careerlink.repository.StudentRepository;
import com.careerlink.service.JobService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public JobResponse createJob(String email, JobRequest request) {
        Company company = getCompany(request.getCompanyId());
        User currentUser = getUserByEmail(email);
        assertCanManageCompany(currentUser, company);

        Job job = new Job();
        applyJobValues(job, request, company, currentUser);

        return mapJob(jobRepository.save(job));
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapJob)
                .toList();
    }

    @Override
    public List<JobResponse> getManagedJobs(String email) {
        User currentUser = getUserByEmail(email);
        if (currentUser.getRole() == Role.ADMIN) {
            return getAllJobs();
        }
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapJob)
                .toList();
    }

    @Override
    public List<JobResponse> getFilteredJobs(String title, Long companyId, String eligibility, Boolean active) {
        return jobRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(job -> title == null || title.isBlank()
                        || job.getTitle().toLowerCase().contains(title.trim().toLowerCase()))
                .filter(job -> companyId == null || job.getCompany().getId().equals(companyId))
                .filter(job -> eligibility == null || eligibility.isBlank()
                        || job.getEligibility().toLowerCase().contains(eligibility.trim().toLowerCase()))
                .filter(job -> active == null || job.isActive() == active)
                .map(this::mapJob)
                .toList();
    }

    @Override
    public JobResponse getJobById(Long jobId) {
        return mapJob(getJobEntity(jobId));
    }

    @Override
    public List<JobResponse> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .map(this::mapJob)
                .toList();
    }

    @Override
    public List<JobResponse> getAvailableJobs(String email, String title, Long companyId, String eligibility) {
        Student student = studentRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return jobRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(Job::isActive)
                .filter(job -> title == null || title.isBlank() || job.getTitle().toLowerCase().contains(title.trim().toLowerCase()))
                .filter(job -> companyId == null || job.getCompany().getId().equals(companyId))
                .filter(job -> eligibility == null || eligibility.isBlank() || job.getEligibility().toLowerCase().contains(eligibility.trim().toLowerCase()))
                .filter(job -> isEligible(student, job))
                .map(job -> mapJob(job, true))
                .toList();
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, String email, JobRequest request) {
        Job job = getJobEntity(jobId);
        Company company = getCompany(request.getCompanyId());
        User currentUser = getUserByEmail(email);
        assertCanManageJob(currentUser, job);
        assertCanManageCompany(currentUser, company);

        applyJobValues(job, request, company, currentUser);
        return mapJob(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, String email) {
        Job job = getJobEntity(jobId);
        assertCanManageJob(getUserByEmail(email), job);
        jobRepository.delete(job);
    }

    private void applyJobValues(Job job, JobRequest request, Company company, User currentUser) {
        job.setTitle(request.getTitle().trim());
        job.setDescription(request.getDescription().trim());
        job.setEligibility(request.getEligibility().trim());
        job.setEligibilityCriteria(request.getEligibility().trim());
        job.setEligibleBranches(request.getEligibleBranches().trim());
        job.setMinimumCgpa(request.getMinimumCgpa());
        job.setGraduationYear(request.getGraduationYear());
        job.setMaximumActiveBacklogs(request.getMaximumActiveBacklogs());
        job.setLocation(job.getLocation() != null ? job.getLocation() : "Remote");
        job.setSalaryPackage(job.getSalaryPackage() != null ? job.getSalaryPackage() : "Confidential");
        job.setApplicationDeadline(job.getApplicationDeadline() != null ? job.getApplicationDeadline() : LocalDate.now().plusDays(30));
        job.setActive(true);
        job.setCompany(company);
        job.setRecruiter(job.getRecruiter() != null ? job.getRecruiter() : currentUser);
    }

    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    private Job getJobEntity(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
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
            throw new AccessDeniedException("Recruiters can post jobs only for their own company");
        }
    }

    private void assertCanManageJob(User currentUser, Job job) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (!job.getRecruiter().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Recruiters can manage only their own job postings");
        }
    }

    private JobResponse mapJob(Job job) {
        return mapJob(job, null);
    }

    private JobResponse mapJob(Job job, Boolean eligibleForCurrentStudent) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .eligibility(job.getEligibility())
                .location(job.getLocation())
                .salaryPackage(job.getSalaryPackage())
                .applicationDeadline(job.getApplicationDeadline())
                .active(job.isActive())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .eligibleBranches(job.getEligibleBranches())
                .minimumCgpa(job.getMinimumCgpa())
                .graduationYear(job.getGraduationYear())
                .maximumActiveBacklogs(job.getMaximumActiveBacklogs())
                .eligibleForCurrentStudent(eligibleForCurrentStudent)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private boolean isEligible(Student student, Job job) {
        if (student.getCgpa() == null || student.getGraduationYear() == null || student.getActiveBacklogs() == null
                || job.getMinimumCgpa() == null || job.getGraduationYear() == null || job.getMaximumActiveBacklogs() == null) {
            return false;
        }
        boolean branchMatches = job.getEligibleBranches().toLowerCase().contains(student.getBranch().toLowerCase())
                || job.getEligibleBranches().toLowerCase().contains("all");
        return branchMatches
                && student.getCgpa().compareTo(job.getMinimumCgpa()) >= 0
                && student.getGraduationYear().equals(job.getGraduationYear())
                && student.getActiveBacklogs() <= job.getMaximumActiveBacklogs();
    }
}
