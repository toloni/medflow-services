package com.medflow.appointmentservice.application.port.out;

import com.medflow.appointmentservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/// Output port for looking up [User] records.
public interface UserRepositoryPort {

    Optional<User> findById(UUID userId);
}
