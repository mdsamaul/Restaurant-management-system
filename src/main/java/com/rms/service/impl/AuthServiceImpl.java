package com.rms.service.impl;
import com.rms.dto.request.LoginRequest;
import com.rms.dto.request.RegisterRequest;
import com.rms.dto.response.AuthResponse;
import com.rms.entity.User;
import com.rms.exception.BadRequestException;
import com.rms.repository.UserRepository;
import com.rms.security.service.JwtService;
import com.rms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already registered: " + req.getEmail());
        User user = User.builder()
            .fullName(req.getFullName()).email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .phone(req.getPhone()).role(User.Role.CUSTOMER).isActive(true).build();
        userRepository.save(user);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.of(accessToken, refreshToken, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new BadRequestException("User not found"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.of(accessToken, refreshToken, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
