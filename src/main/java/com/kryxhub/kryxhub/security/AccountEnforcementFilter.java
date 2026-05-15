package com.kryxhub.kryxhub.security;

import com.kryxhub.kryxhub.enums.AccountStatus;
import com.kryxhub.kryxhub.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AccountEnforcementFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public AccountEnforcementFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            
            String identifier = auth.getName(); 
            var userOptional = userRepository.findByEmail(identifier); 

            if (userOptional.isPresent()) {
                if (userOptional.get().getAccountStatus() == AccountStatus.SUSPENDED) {
                    
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    
                    String jsonError = "{\"error\": \"Forbidden\", \"message\": \"Your account has been suspended by a KRYXHUB Admin. Your token is revoked.\"}";
                    response.getWriter().write(jsonError);
                    return; 
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}