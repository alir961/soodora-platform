package com.soodora.service.auth;

import com.soodora.domain.auth.AuthenticationRepository;
import com.soodora.domain.auth.RegisterConfirmationRepository;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.mail.ConfirmationEmailSender;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.playlist.PlaylistRepository;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserFactory;
import com.soodora.service.user.UserUnityException;

import java.util.AbstractMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance;

import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;
import static com.soodora.domain.user.value.Identifier.Factory.createFromUsername;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.TestConstants.VALID_PASSWORD;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final String SECRET = "secret";

    @Mock
    private AuthenticationRepository authenticationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private OAuthTokenRepository oauthTokenRepository;

    @Mock
    private RegisterConfirmationRepository registerConfirmationRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private AuthorizationMapper authorizationMapper;

    @Mock
    private ConfirmationEmailSender emailSender;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                getInstance(),
                authenticationRepository,
                userRepository,
                tokenRepository,
                oauthTokenRepository,
                registerConfirmationRepository,
                playlistRepository,
                new UserFactory(getInstance()),
                authorizationMapper,
                emailSender);
    }

    @Test
    void givenValidRequest_whenRegisterUser_thenSuccess() {
        // GIVEN
        UserRegisterCommand command = new UserRegisterCommand("username", "user@example.com", VALID_PASSWORD, "John",
                "Doe",
                "external");

        when(userRepository.isUsed(any())).thenReturn(false);

        // WHEN
        authenticationService.registerUser(command);

        // THEN
        verify(emailSender).sendEmail(eq(command.email()), any(), any());
    }

    @Test
    void givenUsedEmail_whenRegisterUser_thenUnityException() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.getEmail().value());
        UserRegisterCommand command = new UserRegisterCommand("username", identifier.value(), VALID_PASSWORD, "John",
                "Doe",
                "external");

        when(userRepository.isUsed(identifier)).thenReturn(true);

        // WHEN
        Executable executable = () -> authenticationService.registerUser(command);

        // THEN
        assertThrows(UserUnityException.class, executable, "Email is already used");
    }

    @Test
    void givenUsedUsername_whenRegisterUser_thenUserUnityException() {
        // GIVEN
        Identifier emailIdentifier = createFromEmail(USER.getEmail().value());
        Identifier usernameIdentifier = createFromUsername(USER.getUsername().value());
        UserRegisterCommand command = new UserRegisterCommand(usernameIdentifier.value(), emailIdentifier.value(),
                VALID_PASSWORD, "John", "Doe",
                "external");

        when(userRepository.isUsed(emailIdentifier)).thenReturn(false);
        when(userRepository.isUsed(usernameIdentifier)).thenReturn(true);

        // WHEN
        Executable executable = () -> authenticationService.registerUser(command);

        // THEN
        assertThrows(UserUnityException.class, executable, "Username is already used");
    }

    @Test
    void givenValidSecret_whenConfirmUser_thenSuccess() {
        // GIVEN
        when(registerConfirmationRepository.hasConfirmation(SECRET)).thenReturn(true);
        when(registerConfirmationRepository.popConfirmation(SECRET))
                .thenReturn(new AbstractMap.SimpleEntry<>(USER, "external"));
        when(userRepository.save(USER)).thenReturn(USER);

        // WHEN
        Executable executable = () -> authenticationService.confirmUserRegistration(SECRET);

        // THEN
        assertDoesNotThrow(executable);
    }

    @Test
    void givenInvalidSecret_whenConfirmUser_thenAuthenticationException() {
        // GIVEN
        when(registerConfirmationRepository.hasConfirmation(SECRET)).thenReturn(false);

        // WHEN
        Executable executable = () -> authenticationService.confirmUserRegistration(SECRET);

        // THEN
        assertThrows(AuthenticationException.class, executable, "Unable to confirm a registration for that secret");
    }

    @Test
    void givenValidEmailCredentials_whenLoginUser_thenSuccess() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.getEmail().value());
        LoginCommand command = new LoginCommand(identifier, "password");

        when(userRepository.find(identifier)).thenReturn(USER);
        when(authenticationRepository.retrieveEncodedPassword(identifier)).thenReturn("password");
        when(tokenRepository.generateToken(USER)).thenReturn("externalToken");

        // WHEN
        String token = authenticationService.loginUser(command);

        // THEN
        assertEquals("externalToken", token);
    }

    @Test
    void givenValidUsernameCredentials_whenLoginUser_thenSuccess() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.getUsername().value());
        LoginCommand command = new LoginCommand(identifier, "password");

        when(userRepository.find(identifier)).thenReturn(USER);
        when(authenticationRepository.retrieveEncodedPassword(identifier)).thenReturn("password");
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
        assertThrows(UnknownUserException.class, executable, "Invalid credentials");
    }

    @Test
    void givenInvalidPassword_whenLoginUser_thenUnknownUserException() {
        // GIVEN
        Identifier identifier = createFromEmail(USER.getEmail().value());
        LoginCommand command = new LoginCommand(identifier, "unknown");

        when(userRepository.find(identifier)).thenReturn(USER);

        // WHEN
        Executable executable = () -> authenticationService.loginUser(command);

        // THEN
        assertThrows(UnknownUserException.class, executable, "Invalid credentials");
    }

}
