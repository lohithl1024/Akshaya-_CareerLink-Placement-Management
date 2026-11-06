package com.careerlink.service;

import java.util.List;

import com.careerlink.dto.user.UserRequest;
import com.careerlink.dto.user.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    UserResponse updateUser(Long userId, UserRequest request);

    void deleteUser(Long userId);
}
