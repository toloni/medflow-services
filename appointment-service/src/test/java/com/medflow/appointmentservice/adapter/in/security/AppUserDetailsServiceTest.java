package com.medflow.appointmentservice.adapter.in.security;

import com.medflow.appointmentservice.adapter.out.persistence.UserRepository;
import com.medflow.appointmentservice.domain.model.Role;
import com.medflow.appointmentservice.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private AppUserDetailsService service;

    @Test
    void userFound_returnsWrappingAppUserDetails() {
        service = new AppUserDetailsService(userRepository);
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("jdoe")
                .passwordHash("hashed")
                .role(Role.DOCTOR)
                .fullName("Jane Doe")
                .build();

        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("jdoe");

        assertThat(result).isInstanceOf(AppUserDetails.class);
        AppUserDetails appUserDetails = (AppUserDetails) result;
        assertThat(appUserDetails.getUserId()).isEqualTo(user.getId());
        assertThat(appUserDetails.getRole()).isEqualTo(Role.DOCTOR);
        assertThat(appUserDetails.getUsername()).isEqualTo("jdoe");
        assertThat(appUserDetails.getPassword()).isEqualTo("hashed");
    }

    @Test
    void userNotFound_throwsUsernameNotFoundException() {
        service = new AppUserDetailsService(userRepository);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost");
    }
}
