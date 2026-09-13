package com.medflow.appointmentservice.adapter.in.security;

import com.medflow.appointmentservice.domain.model.Role;
import com.medflow.appointmentservice.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserDetailsTest {

    @Test
    void delegatesGettersToWrappedUser() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("nurse1")
                .passwordHash("secret-hash")
                .role(Role.NURSE)
                .fullName("Nurse Joy")
                .build();

        AppUserDetails details = new AppUserDetails(user);

        assertThat(details.getUserId()).isEqualTo(id);
        assertThat(details.getRole()).isEqualTo(Role.NURSE);
        assertThat(details.getUsername()).isEqualTo("nurse1");
        assertThat(details.getPassword()).isEqualTo("secret-hash");
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    void authorities_prefixRoleNameWithRolePrefix() {
        User doctorUser = User.builder()
                .id(UUID.randomUUID())
                .username("doc1")
                .passwordHash("hash")
                .role(Role.DOCTOR)
                .fullName("Dr. Strange")
                .build();

        AppUserDetails details = new AppUserDetails(doctorUser);

        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_DOCTOR");
    }
}
