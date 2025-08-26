package com.soodora.service.oauth;

import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.AuthorizationMapper;
import com.soodora.domain.oauth.OAuthTokenRepository;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.google.GoogleFacade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

}
