package com.careerlink.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.careerlink.entity.User;
import com.careerlink.enums.Role;
import com.careerlink.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Creates only the administrator accounts required to access a fresh system.
 * Recruiters, students, companies, jobs, and applications are created through
 * the normal application workflow rather than being populated as demo data.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_PASSWORD = "Admin@CareerLink2026";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        upsertAdmin("Priya Sharma", "admin@careerlink.com", ADMIN_PASSWORD);
        upsertAdmin("Ashwini", "ashwini@tpo.com", "123456");
    }

    private void upsertAdmin(String name, String email, String rawPassword) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(User::new);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.ADMIN);
        user.setEnabled(true);
        userRepository.save(user);
    }
}
