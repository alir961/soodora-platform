package com.grooveconductor.api.auth;

import com.grooveconductor.domain.auth.TokenRepository;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.auth.UserPrincipal;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer "))
            authenticateBearerToken(authorization.substring(7));
        else
            handleAuthenticatedUser();
        filterChain.doFilter(request, response);
    }

    private void handleAuthenticatedUser() {
        Authentication authentication = getContext().getAuthentication();

        if (authentication == null)
            return;
        if (authentication instanceof UsernamePasswordAuthenticationToken emailAuthentication) {
            UserPrincipal principal = (UserPrincipal) emailAuthentication.getPrincipal();

            setContextAuthentication(principal.user());
        }
    }

    private void authenticateBearerToken(String token) {
        String email = tokenRepository.getSubjectFromToken(token);
        UserDTO user = userRepository.find(createFromEmail(email));
        boolean isAuthenticated = getContext().getAuthentication() != null;

        if (email == null || user == null || isAuthenticated)
            return;
        setContextAuthentication(user);
    }

    private void setContextAuthentication(UserDTO user) {
        List<SimpleGrantedAuthority> authorities = user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .toList();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

        getContext().setAuthentication(authentication);
    }

}
