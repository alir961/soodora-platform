package com.grooveconductor.application.auth;

import com.grooveconductor.domain.auth.LoginCommand;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.application.user.UnknownUserException;
import com.grooveconductor.application.user.UserUnityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.crypto.password.NoOpPasswordEncoder.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserRepository userRepository;

    private AuthenticationService authenticationService;

    private static final UserDTO USER = buildDefaultUser();

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(jwtTokenService, userRepository, getInstance());
    }

    @Test
    void registerUser_Success() {
        // GIVEN
        RegisterCommand command = new RegisterCommand("username", createFromEmail("user@example.com"), "password", "John", "Doe");

        when(userRepository.isUsed(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(USER);

        // WHEN
        UserDTO user = authenticationService.registerUser(command);

        // THEN
        assertEquals(USER, user);
    }

    @Test
    void registerUser_NotUnique() {
        // GIVEN
        RegisterCommand command = new RegisterCommand("username", createFromEmail("user@example.com"), "password", "John", "Doe");

        when(userRepository.isUsed(any())).thenReturn(true);

        // WHEN
        Executable executable = () -> authenticationService.registerUser(command);

        // THEN
        assertThrows(UserUnityException.class, executable);
    }

    @Test
    void loginUser_Success() {
        // GIVEN
        LoginCommand command = new LoginCommand(createFromEmail("user@example.com"), "password");

        when(userRepository.find(any())).thenReturn(USER);
        when(userRepository.retrieveEncodedPassword(any())).thenReturn("password");
        when(jwtTokenService.generateToken((any()))).thenReturn("token");

        // WHEN
        String token = authenticationService.loginUser(command);

        // THEN
        assertEquals("token", token);
    }

    @Test
    void loginUser_InvalidEmail() {
        // GIVEN
        LoginCommand command = new LoginCommand(createFromEmail("unknown@example.com"), "password");

        when(userRepository.find(any())).thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> authenticationService.loginUser(command);

        // THEN
        assertThrows(UnknownIdentifierException.class, executable);
    }

    @Test
    void loginUser_InvalidPassword() {
        // GIVEN
        LoginCommand command = new LoginCommand(createFromEmail("user@example.com"), "unknown");

        when(userRepository.find(any())).thenReturn(USER);
        when(userRepository.retrieveEncodedPassword(any())).thenReturn("password");

        // WHEN
        Executable executable = () -> authenticationService.loginUser(command);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

}