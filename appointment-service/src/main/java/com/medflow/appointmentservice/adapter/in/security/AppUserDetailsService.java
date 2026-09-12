package com.medflow.appointmentservice.adapter.in.security;


import com.medflow.appointmentservice.adapter.out.persistence.UserRepository;
import com.medflow.appointmentservice.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/// Loads users for Spring Security authentication by username, backed by the
/// JPA [UserRepository].
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));
        return new AppUserDetails(user);
    }
}
