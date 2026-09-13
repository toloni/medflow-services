package com.medflow.appointmentservice.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/// An authenticatable account, either a [Doctor], a [Patient], or an
/// unassociated staff member (e.g. a nurse), distinguished by [Role].
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "full_name", nullable = false)
    private String fullName;
}
