package com.grooveconductor.api.auth;

import com.grooveconductor.domain.auth.TokenRepository;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

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
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer "))
            authenticateBearerToken(request, authorization.substring(7));
        filterChain.doFilter(request, response);
    }

    private void authenticateBearerToken(HttpServletRequest request, String token) {
        String email = tokenRepository.getEmailFromToken(token);
        UserDTO user = userRepository.find(createFromEmail(email));
        boolean isAuthenticated = getContext().getAuthentication() != null;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                new ArrayList<>()
        );

        if (email == null || user == null || isAuthenticated) return;
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        getContext().setAuthentication(authentication);
        log.info("User {} authenticated", email);
    }

}
