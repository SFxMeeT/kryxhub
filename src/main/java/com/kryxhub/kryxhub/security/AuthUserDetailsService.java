package com.kryxhub.kryxhub.security;

import com.kryxhub.kryxhub.repository.UserRepository;
import com.kryxhub.kryxhub.enums.AccountStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.LockedException;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new LockedException("Your account has been suspended by an KRYXHUB.");
        }

        return new AuthUser(user);
    }
}