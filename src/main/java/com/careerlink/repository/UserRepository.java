package com.careerlink.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerlink.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
