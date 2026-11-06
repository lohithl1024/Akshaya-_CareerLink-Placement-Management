package com.careerlink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerlink.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);

    List<Application> findAllByOrderByCreatedAtDesc();

    List<Application> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Application> findByJobIdOrderByCreatedAtDesc(Long jobId);

    List<Application> findByJobRecruiterIdOrderByCreatedAtDesc(Long recruiterId);
}
