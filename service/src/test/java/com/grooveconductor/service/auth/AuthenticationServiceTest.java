package com.grooveconductor.service.auth;

import com.grooveconductor.domain.auth.LoginCommand;
import com.grooveconductor.domain.auth.RegisterCommand;
import com.grooveconductor.domain.auth.TokenRepository;
import com.grooveconductor.domain.oauth.OAuthTokenRepository;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UserUnityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.test.TestConstants.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private OAuthTokenRepository oauthTokenRepository;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                getInstance(),
                userRepository,
                tokenRepository,
                oauthTokenRepository
        );
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
        Identifier identifier = createFromEmail(USER.email());

        when(userRepository.find(identifier)).thenReturn(USER);
        when(userRepository.retrieveEncodedPassword(identifier)).thenReturn("password");
        when(tokenRepository.generateToken(USER)).thenReturn("token");

        // WHEN
        String token = authenticationService.loginUser(command);

        // THEN
        assertEquals("token", token);
    }

    @Test
    void loginUser_InvalidEmail() {
        // GIVEN
        Identifier identifier = createFromEmail("unknown@example.com");
        LoginCommand command = new LoginCommand(identifier, "password");

        when(userRepository.find(identifier)).thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> authenticationService.loginUser(command);

        // THEN
        assertThrows(UnknownIdentifierException.class, executable);
    }

    @Test
    void loginUser_InvalidPassword() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.email());
        LoginCommand command = new LoginCommand(identifier, "unknown");

        when(userRepository.find(identifier)).thenReturn(USER);
        when(userRepository.retrieveEncodedPassword(identifier)).thenReturn("password");

        // WHEN
        Executable executable = () -> authenticationService.loginUser(command);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

}