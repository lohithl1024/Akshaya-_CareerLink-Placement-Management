package com.careerlink.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerlink.dto.user.UserRequest;
import com.careerlink.dto.user.UserResponse;
import com.careerlink.entity.User;
import com.careerlink.exception.BadRequestException;
import com.careerlink.exception.ResourceNotFoundException;
import com.careerlink.repository.UserRepository;
import com.careerlink.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("User email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        return mapUser(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUser)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return mapUser(getUserEntity(userId));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) {
        User user = getUserEntity(userId);
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, userId)) {
            throw new BadRequestException("User email already exists");
        }

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return mapUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.delete(getUserEntity(userId));
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse mapUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .studentId(user.getStudent() != null ? user.getStudent().getId() : null)
                .build();
    }
}
