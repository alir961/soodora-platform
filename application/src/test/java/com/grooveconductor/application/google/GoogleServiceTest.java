package com.grooveconductor.application.google;

import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.grooveconductor.application.google.GoogleService.AUTH_URL;
import static com.grooveconductor.application.google.GoogleService.SCOPE;
import static com.grooveconductor.test.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleServiceTest {

    @Mock
    private GoogleAPIClient googleClient;

    private GoogleService googleService;

    private final static String QUERY_PARAMS = "?client_id=clientId&redirect_uri=redirectUri&response_type=code&access_type=offline&prompt=consent";

    @BeforeEach
    void setUp() {
        googleService = new GoogleService(googleClient);
    }

    @Test
    void getOAuthAuthorizationFlowURI_ValidURI() {
        // GIVEN
        String state = "state";

        // WHEN
        URI uri = googleService.getOAuthAuthorizationFlowURI(state);

        // THEN
        assertNotNull(uri);
        assertEquals(AUTH_URL + QUERY_PARAMS + "&scope=" + SCOPE + "&state=" + state, uri.toString());
    }

    @Test
    void exchangeOAuthToken_Success() {
        // GIVEN
        Map<String, String> body = defaultBody();

        body.put("code", ACCESS_TOKEN);
        body.put("grant_type", "authorization_code");
        body.put("redirect_uri", "redirectUri");
        when(googleClient.callTokenAPI(body)).thenReturn(AUTHORIZATION);

        // WHEN
        RefreshableAuthorization authorization = googleService.exchangeOAuthToken(ACCESS_TOKEN);

        // THEN
        assertNotNull(authorization);
        assertEquals(AUTHORIZATION, authorization);
    }

    @Test
    void refreshOAuthToken_Success() {
        // GIVEN
        Map<String, String> body = defaultBody();

        body.put("refresh_token", REFRESH_TOKEN);
        body.put("grant_type", "refresh_token");
        when(googleClient.callTokenAPI(body)).thenReturn(AUTHORIZATION);

        // WHEN
        RefreshableAuthorization authorization = googleService.refreshOAuthToken(REFRESH_TOKEN);

        // THEN
        assertNotNull(authorization);
        assertEquals(AUTHORIZATION, authorization);
    }

    private Map<String, String> defaultBody() {
        Map<String, String> body = new HashMap<>();

        body.put("client_id", "clientId");
        body.put("client_secret", "clientSecret");
        return body;
    }

}