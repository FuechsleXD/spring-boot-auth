package com.dennis.auth.models;

import java.time.Instant;

import com.dennis.auth.utils.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(columnDefinition = "TEXT") // BCrypt hash is 60 characters
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant lastLogin;
}
