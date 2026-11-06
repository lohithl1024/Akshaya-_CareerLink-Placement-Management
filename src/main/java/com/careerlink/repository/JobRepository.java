package com.careerlink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerlink.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByOrderByCreatedAtDesc();

    List<Job> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Job> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);
}
