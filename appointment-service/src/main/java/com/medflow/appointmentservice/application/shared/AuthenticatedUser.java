package com.medflow.appointmentservice.application.shared;

import com.medflow.appointmentservice.domain.model.Role;

import java.util.UUID;

/// Identity of the authenticated caller driving a use case, used for
/// role-based access checks.
public record AuthenticatedUser(UUID userId, Role role) {
}
