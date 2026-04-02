package com.rms.service;
import com.rms.dto.response.UserResponse;
import java.util.List;
public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getProfile(String email);
    void toggleUserStatus(Long id);
}
