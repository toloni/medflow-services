package com.medflow.appointmentservice.adapter.out.persistence;

import com.medflow.appointmentservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/// Spring Data JPA repository for [User], also used directly by the security
/// adapter to look up credentials by username.
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
}
