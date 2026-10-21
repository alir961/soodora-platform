package com.soodora.service.oauth;

import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.AuthenticationException;
import com.soodora.service.google.GoogleFacade;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class OAuthTokenServiceTest {

    @Mock
    private OAuthTokenRepository oauthTokenRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoogleFacade googleFacade;

    private OAuthTokenService oauthTokenService;

    @Mock
    private AuthorizationMapper authorizationMapper;

    @BeforeEach
    void setUp() {
        oauthTokenService = new OAuthTokenService(
                oauthTokenRepository,
                tokenRepository,
                userRepository,
                authorizationMapper,
                googleFacade);
    }

    @Test
    void retrieveUserRefreshToken_Success() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(true);
        when(oauthTokenRepository.retrieveRefreshToken(GOOGLE, USER)).thenReturn(REFRESH_TOKEN);

        // WHEN
        String refreshToken = oauthTokenService.retrieveUserRefreshToken(GOOGLE, USER);

        // THEN
        assertEquals(REFRESH_TOKEN, refreshToken);
    }

    @Test
    void retrieveUserRefreshToken_Throw() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(false);

        // WHEN
        Executable executable = () -> oauthTokenService.retrieveUserRefreshToken(GOOGLE, USER);

        // THEN
        assertThrows(OAuthFlowException.class, executable);
    }

    @Test
    void retrieveUserAccessToken_Success() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(true);
        when(oauthTokenRepository.retrieveRefreshToken(GOOGLE, USER)).thenReturn(REFRESH_TOKEN);
        when(googleFacade.refreshOAuthToken(REFRESH_TOKEN)).thenReturn(AUTHORIZATION);

        // WHEN
        String accessToken = oauthTokenService.retrieveUserAccessToken(GOOGLE, USER);

        // THEN
        assertEquals(ACCESS_TOKEN, accessToken);
    }

    @Test
    void retrieveUserAccessToken_Throw() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(false);

        // WHEN
        Executable executable = () -> oauthTokenService.retrieveUserAccessToken(GOOGLE, USER);

        // THEN
        assertThrows(OAuthFlowException.class, executable);
    }

    @Test
    void retrieveUserAccessToken_RefreshesOnceTheCachedTokenExpires() {
        // GIVEN
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        RefreshableAuthorization oneHour = new RefreshableAuthorization(EXTERNAL_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN,
                3600);
        RefreshableAuthorization renewed = new RefreshableAuthorization(EXTERNAL_TOKEN, "renewed", REFRESH_TOKEN, 3600);
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(true);
        when(oauthTokenRepository.retrieveRefreshToken(GOOGLE, USER)).thenReturn(REFRESH_TOKEN);
        when(googleFacade.refreshOAuthToken(REFRESH_TOKEN)).thenReturn(oneHour, renewed);

        // WHEN
        oauthTokenService.setClock(Clock.fixed(start, ZoneOffset.UTC));
        String first = oauthTokenService.retrieveUserAccessToken(GOOGLE, USER);
        oauthTokenService.setClock(Clock.fixed(start.plus(Duration.ofMinutes(30)), ZoneOffset.UTC));
        String cached = oauthTokenService.retrieveUserAccessToken(GOOGLE, USER);
        oauthTokenService.setClock(Clock.fixed(start.plus(Duration.ofMinutes(61)), ZoneOffset.UTC));
        String afterExpiry = oauthTokenService.retrieveUserAccessToken(GOOGLE, USER);

        // THEN
        assertEquals(ACCESS_TOKEN, first);
        assertEquals(ACCESS_TOKEN, cached);
        assertEquals("renewed", afterExpiry);
        verify(googleFacade, times(2)).refreshOAuthToken(REFRESH_TOKEN);
    }

    @Test
    void googleSignIn_GivesAOneTimeCodeInsteadOfTheSessionToken() {
        // GIVEN
        String state = startSignIn();
        when(googleFacade.exchangeOAuthToken("code")).thenReturn(AUTHORIZATION);
        when(tokenRepository.getSubjectFromGoogleToken(EXTERNAL_TOKEN)).thenReturn("google-id");
        when(oauthTokenRepository.retrieveUser(GOOGLE, "google-id")).thenReturn(USER);
        when(tokenRepository.generateToken(USER)).thenReturn(JWT_TOKEN);

        // WHEN
        String redirect = oauthTokenService.processGoogleAuthentication("code", state);

        // THEN
        assertFalse(redirect.contains(JWT_TOKEN));
        String code = redirect.substring(redirect.indexOf("?code=") + "?code=".length());
        assertEquals(JWT_TOKEN, oauthTokenService.exchangeLoginCode(code));
        assertThrows(AuthenticationException.class, () -> oauthTokenService.exchangeLoginCode(code));
    }

    @Test
    void callback_WithAnUnknownOrReusedState_IsRejected() {
        // GIVEN
        String state = startSignIn();
        when(googleFacade.exchangeOAuthToken("code")).thenReturn(AUTHORIZATION);
        when(tokenRepository.getSubjectFromGoogleToken(EXTERNAL_TOKEN)).thenReturn("google-id");
        when(oauthTokenRepository.retrieveUser(GOOGLE, "google-id")).thenReturn(USER);
        oauthTokenService.processGoogleAuthentication("code", state);

        // WHEN & THEN
        assertTrue(oauthTokenService.processGoogleAuthentication("code", state).endsWith("?error=oauth"));
        assertTrue(oauthTokenService.processGoogleAuthentication("code", "forged").endsWith("?error=oauth"));
        verify(googleFacade, times(1)).exchangeOAuthToken("code");
    }

    @Test
    void callback_AfterTheStateExpired_IsRejected() {
        // GIVEN
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        oauthTokenService.setClock(Clock.fixed(start, ZoneOffset.UTC));
        String state = startSignIn();

        // WHEN
        oauthTokenService.setClock(Clock.fixed(start.plus(Duration.ofMinutes(11)), ZoneOffset.UTC));

        // THEN
        assertTrue(oauthTokenService.processGoogleAuthentication("code", state).endsWith("?error=oauth"));
        verify(googleFacade, never()).exchangeOAuthToken(any());
    }

    @Test
    void linkingAccount_DoesNotPutTheSessionTokenInTheGoogleUrl() {
        // GIVEN
        ArgumentCaptor<String> state = ArgumentCaptor.forClass(String.class);
        when(googleFacade.getOAuthAuthorizationFlowURI(state.capture())).thenReturn(URI.create("https://google"));

        // WHEN
        oauthTokenService.retrieveUserGoogleOAuthURI(USER);

        // THEN
        assertTrue(state.getValue().length() >= 40);
        assertFalse(state.getValue().contains(USER.getSlug()));
    }

    @Test
    void linkingAccount_WithoutUser_Throws() {
        assertThrows(AuthenticationException.class, () -> oauthTokenService.retrieveUserGoogleOAuthURI(null));
    }

    private String startSignIn() {
        ArgumentCaptor<String> state = ArgumentCaptor.forClass(String.class);
        when(googleFacade.getOAuthAuthorizationFlowURI(state.capture())).thenReturn(URI.create("https://google"));
        oauthTokenService.retrieveGogoleUnauthorizedOAuthURI();
        return state.getValue();
    }

}
