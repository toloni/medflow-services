package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.application.port.out.UserRepositoryPort;
import com.medflow.appointmentservice.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/// Adapts [UserRepositoryPort] to Spring Data JPA via [UserRepository].
@Component
@RequiredArgsConstructor
class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }
}
