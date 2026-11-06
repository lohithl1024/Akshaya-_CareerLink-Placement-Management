package com.careerlink.service;

import java.util.List;

import com.careerlink.dto.job.JobRequest;
import com.careerlink.dto.job.JobResponse;

public interface JobService {

    JobResponse createJob(String email, JobRequest request);

    List<JobResponse> getAllJobs();

    List<JobResponse> getManagedJobs(String email);

    List<JobResponse> getFilteredJobs(String title, Long companyId, String eligibility, Boolean active);

    JobResponse getJobById(Long jobId);

    List<JobResponse> getJobsByCompany(Long companyId);

    List<JobResponse> getAvailableJobs(String email, String title, Long companyId, String eligibility);

    JobResponse updateJob(Long jobId, String email, JobRequest request);

    void deleteJob(Long jobId, String email);
}
