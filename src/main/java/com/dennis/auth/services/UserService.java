package com.dennis.auth.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dennis.auth.models.User;
import com.dennis.auth.repository.UserRepository;
import com.dennis.auth.utils.Role;

import lombok.RequiredArgsConstructor;

import com.dennis.auth.dtos.UserDto;
import com.dennis.auth.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Register a new user with encrypted password
     */
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Authenticate user with email and password
     */
    public UserDto authenticateUser(UserDto userDto) {
        Optional<User> userOptional = userRepository.findByEmail(userDto.email());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Compare plaintext password with BCrypt hash
        if (!passwordEncoder.matches(userDto.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setLastLogin(Instant.now());
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Find user by email
     */
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDto);
    }

    /**
     * Find user by ID
     */
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    /**
     * Find all users
     */
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    /**
     * Update user password
     */
    public UserDto updatePassword(Long userId, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Update user information (email and role)
     */
    public UserDto update(Long userId, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Update email if provided and not already taken
        if (userDto.email() != null && !userDto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDto.email())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userDto.email());
        }

        // Update role if provided
        if (userDto.role() != null) {
            user.setRole(Role.valueOf(userDto.role()));
        }

        user.setUpdatedAt(Instant.now());
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Update last login timestamp for a user
     */
    public void updateLastLogin(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        user.setLastLogin(Instant.now());
        userRepository.save(user);
    }

    /**
     * Delete user by ID
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Delete user by ID (alias for delete)
     */
    public void deleteById(Long id) {
        delete(id);
    }

}
