package com.projectSpring.ecom.service;

import com.projectSpring.ecom.dto.AuthResponse;
import com.projectSpring.ecom.dto.UserRequest;
import com.projectSpring.ecom.entity.User;
import com.projectSpring.ecom.mapper.UserMapper;
import com.projectSpring.ecom.repository.UserRepository;
import com.projectSpring.ecom.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthResponse register(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : User.Role.CUSTOMER)
                .active(true)
                .createdAt(System.currentTimeMillis())
                .build();

        User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(savedUser))
                .build();
    }

    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    public AuthResponse refreshToken(String oldToken) {
        // Simple implementation for now
        String userEmail = jwtService.extractUsername(oldToken.substring(7));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtService.isTokenValid(oldToken.substring(7), user)) {
            String accessToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(oldToken.substring(7))
                    .user(userMapper.toResponse(user))
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public void logout(String token) {
        // Implementation for token blacklisting if needed
    }
}