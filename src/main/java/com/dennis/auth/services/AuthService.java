package com.dennis.auth.services;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dennis.auth.dtos.LoginRequest;
import com.dennis.auth.dtos.LoginResponse;
import com.dennis.auth.dtos.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        UserDto userDto = userService.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), userDto.password())) {
            throw new RuntimeException("Invalid credentials");
        }

        userService.updateLastLogin(userDto.id());

        String token = jwtService.generateToken(userDto);

        return new LoginResponse(token, Instant.now().plusSeconds(3600), userDto.role());

    }
}
