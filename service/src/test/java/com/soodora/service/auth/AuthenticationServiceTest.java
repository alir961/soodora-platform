package com.soodora.service.auth;

import com.soodora.domain.auth.LoginCommand;
import com.soodora.domain.auth.RegisterCommand;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance;

import static com.soodora.domain.user.IdentifierFactory.createFromEmail;
import static com.soodora.domain.user.IdentifierFactory.createFromUsername;
import static com.soodora.test.TestConstants.USER;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private OAuthTokenRepository oauthTokenRepository;

    @Mock
    private AuthorizationMapper authorizationMapper;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                getInstance(),
                userRepository,
                tokenRepository,
                oauthTokenRepository,
                authorizationMapper);
    }

    @Test
    void givenValidRequest_whenRegisterUser_thenSuccess() {
        // GIVEN
        RegisterCommand command = new RegisterCommand("username", "user@example.com", "password", "John", "Doe",
                "external");

        when(userRepository.isUsed(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(USER);

        // WHEN
        UserDTO user = authenticationService.registerUser(command);

        // THEN
        assertEquals(USER, user);
    }

    @Test
    void givenUsedEmail_whenRegisterUser_thenUnityException() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.email());
        RegisterCommand command = new RegisterCommand("username", identifier.getValue(), "password", "John", "Doe",
                "external");

        when(userRepository.isUsed(identifier)).thenReturn(true);

        // WHEN
        Executable executable = () -> authenticationService.registerUser(command);

        // THEN
        assertThrows(UserUnityException.class, executable);
    }

    @Test
    void givenUsedUsername_whenRegisterUser_thenUserUnityException() {
        // GIVEN
        Identifier emailIdentifier = createFromEmail(USER.email());
        Identifier usernameIdentifier = createFromUsername(USER.username());
        RegisterCommand command = new RegisterCommand(usernameIdentifier.getValue(), emailIdentifier.getValue(),
                "password", "John", "Doe",
                "external");

        when(userRepository.isUsed(emailIdentifier)).thenReturn(false);
        when(userRepository.isUsed(usernameIdentifier)).thenReturn(true);

        // WHEN
        Executable executable = () -> authenticationService.registerUser(command);

        // THEN
        assertThrows(UserUnityException.class, executable);
    }

    @Test
    void givenValidEmailCredentials_whenLoginUser_thenSuccess() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.email());
        LoginCommand command = new LoginCommand(identifier, "password");

        when(userRepository.find(identifier)).thenReturn(USER);
        when(userRepository.retrieveEncodedPassword(identifier)).thenReturn("password");
        when(tokenRepository.generateToken(USER)).thenReturn("externalToken");

        // WHEN
        String token = authenticationService.loginUser(command);

        // THEN
        assertEquals("externalToken", token);
    }

    @Test
    void givenValidUsernameCredentials_whenLoginUser_thenSuccess() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.username());
        LoginCommand command = new LoginCommand(identifier, "password");

        when(userRepository.find(identifier)).thenReturn(USER);
        when(userRepository.retrieveEncodedPassword(identifier)).thenReturn("password");
        when(tokenRepository.generateToken(USER)).thenReturn("externalToken");

        // WHEN
        String token = authenticationService.loginUser(command);

        // THEN
        assertEquals("externalToken", token);
    }

    @Test
    void givenInvalidEmail_whenLoginUser_thenUnknownUserException() {
        // GIVEN
        Identifier identifier = createFromEmail("unknown@example.com");
        LoginCommand command = new LoginCommand(identifier, "password");

        when(userRepository.find(identifier)).thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> authenticationService.loginUser(command);

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void givenInvalidPassword_whenLoginUser_thenUnknownUserException() {
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
