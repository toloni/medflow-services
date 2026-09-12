package com.medflow.appointmentservice.application.port.in;

import com.medflow.appointmentservice.domain.model.Role;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, Role role) {
}
