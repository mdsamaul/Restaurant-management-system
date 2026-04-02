package com.rms.service.impl;
import com.rms.dto.response.UserResponse;
import com.rms.exception.ResourceNotFoundException;
import com.rms.repository.UserRepository;
import com.rms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override @Transactional(readOnly=true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).collect(Collectors.toList()); }

    @Override @Transactional(readOnly=true)
    public UserResponse getUserById(Long id) {
        return UserResponse.from(userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id))); }

    @Override @Transactional(readOnly=true)
    public UserResponse getProfile(String email) {
        return UserResponse.from(userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"))); }

    @Override public void toggleUserStatus(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }
}
