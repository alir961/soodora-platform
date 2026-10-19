package com.soodora.api.auth;

import com.soodora.domain.auth.AuthenticationTokenException;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;

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

import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;

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
        String email;

        try {
            email = tokenRepository.getSubjectFromToken(token);
        } catch (AuthenticationTokenException exception) {
            // Expired, forged or malformed: continue unauthenticated instead of failing
            // with a 500
            return;
        }
        User user = userRepository.find(createFromEmail(email));
        boolean isAuthenticated = getContext().getAuthentication() != null;

        if (email == null || user == null || isAuthenticated)
            return;
        setContextAuthentication(user);
    }

    private void setContextAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.completeRole()))
                .toList();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

        getContext().setAuthentication(authentication);
    }

}
