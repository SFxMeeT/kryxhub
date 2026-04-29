package com.kryxhub.kryxhub.security;

import com.kryxhub.kryxhub.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(AuthUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
