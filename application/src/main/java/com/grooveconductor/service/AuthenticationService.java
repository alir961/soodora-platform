package com.grooveconductor.service;

import com.grooveconductor.domain.command.LoginCommand;
import com.grooveconductor.domain.command.RegisterCommand;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.repository.UserRepository;
import com.grooveconductor.exception.AuthenticationException;
import com.grooveconductor.exception.UnknownUserException;
import com.grooveconductor.exception.UserUnityException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromEmail;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO registerUser(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());

        if (userRepository.isUsed(command.email())) throw new UserUnityException("Email is already used");
        return userRepository.save(command.updatePassword(encodedPassword));
    }

    public String loginUser(LoginCommand command) {
        UserDTO user = userRepository.find(command.email());
        String password = userRepository.retrieveEncodedPassword(command.email());

        if (!passwordEncoder.matches(command.password(), password))
            throw new UnknownUserException("Invalid password");
        return jwtTokenService.generateToken(user);
    }

    public UserDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) throw new AuthenticationException("User must be authenticated");
        return userRepository.find(createFromEmail(authentication.getPrincipal().toString()));
    }

}
