package com.grooveconductor.api.auth;

import com.grooveconductor.service.auth.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer "))
            authenticateBearerToken(request, authorization.substring(7));
        filterChain.doFilter(request, response);
    }

    private void authenticateBearerToken(HttpServletRequest request, String token) {
        String email = jwtTokenService.getEmailFromToken(token);
        boolean isAuthenticated = getContext().getAuthentication() != null;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

        if (email == null || isAuthenticated) return;
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        getContext().setAuthentication(authentication);
    }

}
