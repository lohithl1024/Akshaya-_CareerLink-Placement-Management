package com.careerlink.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerlink.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByUserEmailIgnoreCase(String email);

    boolean existsByUserId(Long userId);
}
