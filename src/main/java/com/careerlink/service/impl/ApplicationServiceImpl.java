package com.careerlink.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import com.careerlink.dto.application.ApplicationRequest;
import com.careerlink.dto.application.ApplicationResponse;
import com.careerlink.dto.application.ApplicationStatusUpdateRequest;
import com.careerlink.entity.Application;
import com.careerlink.entity.Job;
import com.careerlink.entity.Student;
import com.careerlink.entity.User;
import com.careerlink.enums.ApplicationStatus;
import com.careerlink.enums.Role;
import com.careerlink.exception.BadRequestException;
import com.careerlink.exception.ResourceNotFoundException;
import com.careerlink.repository.ApplicationRepository;
import com.careerlink.repository.JobRepository;
import com.careerlink.repository.StudentRepository;
import com.careerlink.repository.UserRepository;
import com.careerlink.service.ApplicationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApplicationResponse createApplication(String email, ApplicationRequest request) {
        Student student = studentRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for authenticated user"));
        Job job = getJob(request.getJobId());

        if (!meetsEligibility(student, job)) {
            throw new BadRequestException("You do not meet this job's mandatory eligibility criteria");
        }

        if (applicationRepository.existsByStudentIdAndJobId(student.getId(), job.getId())) {
            throw new BadRequestException("Student has already applied for this job");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        return mapApplication(applicationRepository.save(application));
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapApplication)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getManagedApplications(String email) {
        User currentUser = getUserByEmail(email);
        if (currentUser.getRole() == Role.ADMIN) {
            return getAllApplications();
        }
        return applicationRepository.findByJobRecruiterIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapApplication)
                .toList();
    }

    @Override
    public ApplicationResponse getApplicationById(Long applicationId, String email) {
        Application application = getApplicationEntity(applicationId);
        assertCanManageApplication(getUserByEmail(email), application);
        return mapApplication(application);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(String email) {
        Student student = studentRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for authenticated user"));
        return applicationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(this::mapApplication)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByStudent(Long studentId) {
        return applicationRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::mapApplication)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, String email) {
        Job job = getJob(jobId);
        assertCanManageJob(getUserByEmail(email), job);
        return applicationRepository.findByJobIdOrderByCreatedAtDesc(jobId).stream()
                .map(this::mapApplication)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, String email, ApplicationStatusUpdateRequest request) {
        Application application = getApplicationEntity(applicationId);
        assertCanManageApplication(getUserByEmail(email), application);
        application.setStatus(request.getStatus());
        return mapApplication(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public void deleteApplication(Long applicationId) {
        applicationRepository.delete(getApplicationEntity(applicationId));
    }

    private Application getApplicationEntity(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private Job getJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertCanManageApplication(User currentUser, Application application) {
        assertCanManageJob(currentUser, application.getJob());
    }

    private void assertCanManageJob(User currentUser, Job job) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (!job.getRecruiter().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Recruiters can review applications only for their own jobs");
        }
    }

    private ApplicationResponse mapApplication(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .studentId(application.getStudent().getId())
                .studentName(application.getStudent().getUser().getName())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .meetsEligibility(meetsEligibility(application.getStudent(), application.getJob()))
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private boolean meetsEligibility(Student student, Job job) {
        if (student.getCgpa() == null || student.getGraduationYear() == null || student.getActiveBacklogs() == null
                || job.getMinimumCgpa() == null || job.getGraduationYear() == null || job.getMaximumActiveBacklogs() == null) {
            return false;
        }
        String eligibleBranches = job.getEligibleBranches() == null ? "" : job.getEligibleBranches().toLowerCase();
        boolean branchMatches = eligibleBranches.contains("all branches")
                || eligibleBranches.contains(student.getBranch().toLowerCase());
        return branchMatches
                && student.getCgpa().compareTo(job.getMinimumCgpa()) >= 0
                && student.getGraduationYear().equals(job.getGraduationYear())
                && student.getActiveBacklogs() <= job.getMaximumActiveBacklogs();
    }
}
