package com.dennis.auth.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dennis.auth.repository.UserRepository;
import com.dennis.auth.utils.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteInactiveUserService {

    private final UserRepository userRepository;

    // Delete inactive guest users who haven't logged in for 7 days at sunday 3 AM
    @Scheduled(cron = "0 0 3 * * SUN")
    public void deleteInactiveGuests() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        userRepository.deleteByRoleAndLastLoginBefore(Role.GUEST.toString(), cutoff);
    }

}
