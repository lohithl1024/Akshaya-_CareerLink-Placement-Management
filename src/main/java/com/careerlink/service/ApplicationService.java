package com.careerlink.service;

import java.util.List;

import com.careerlink.dto.application.ApplicationRequest;
import com.careerlink.dto.application.ApplicationResponse;
import com.careerlink.dto.application.ApplicationStatusUpdateRequest;

public interface ApplicationService {

    ApplicationResponse createApplication(String email, ApplicationRequest request);

    List<ApplicationResponse> getAllApplications();

    List<ApplicationResponse> getManagedApplications(String email);

    ApplicationResponse getApplicationById(Long applicationId, String email);

    List<ApplicationResponse> getMyApplications(String email);

    List<ApplicationResponse> getApplicationsByStudent(Long studentId);

    List<ApplicationResponse> getApplicationsByJob(Long jobId, String email);

    ApplicationResponse updateApplicationStatus(Long applicationId, String email, ApplicationStatusUpdateRequest request);

    void deleteApplication(Long applicationId);
}
