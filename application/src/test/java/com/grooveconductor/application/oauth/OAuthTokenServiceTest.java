package com.grooveconductor.application.oauth;

import com.grooveconductor.application.google.GoogleService;
import com.grooveconductor.domain.oauth.OAuthTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.test.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthTokenServiceTest {

    @Mock
    private OAuthTokenRepository oauthTokenRepository;

    @Mock
    private GoogleService googleService;

    private OAuthTokenService oauthTokenService;

    @BeforeEach
    void setUp() {
        oauthTokenService = new OAuthTokenService(oauthTokenRepository, googleService);
    }

    @Test
    void updateUserAuthorization_SuccessCreate() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(false);

        // WHEN
        oauthTokenService.updateUserAuthorization(GOOGLE, USER, AUTHORIZATION);

        // THEN
        verify(oauthTokenRepository, times(0)).deleteAuthorization(GOOGLE, USER);
        verify(oauthTokenRepository, times(1)).createAuthorization(GOOGLE, USER, REFRESH_TOKEN);
    }

    @Test
    void updateUserAuthorization_SuccessUpdate() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(true);

        // WHEN
        oauthTokenService.updateUserAuthorization(GOOGLE, USER, AUTHORIZATION);

        // THEN
        verify(oauthTokenRepository, times(1)).deleteAuthorization(GOOGLE, USER);
        verify(oauthTokenRepository, times(1)).createAuthorization(GOOGLE, USER, REFRESH_TOKEN);
    }

    @Test
    void retrieveUserRefreshToken_Success() {
        // GIVEN
        when(oauthTokenRepository.hasAuthorization(GOOGLE, USER)).thenReturn(true);
        when(oauthTokenRepository.retrieveAuthorization(GOOGLE, USER)).thenReturn(REFRESH_TOKEN);

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
        when(oauthTokenRepository.retrieveAuthorization(GOOGLE, USER)).thenReturn(REFRESH_TOKEN);
        when(googleService.refreshOAuthToken(REFRESH_TOKEN)).thenReturn(AUTHORIZATION);

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