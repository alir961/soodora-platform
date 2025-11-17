package com.soodora.service.google;

import com.soodora.domain.oauth.RefreshableAuthorization;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import static com.soodora.service.google.GoogleFacade.AUTH_URL;
import static com.soodora.service.google.GoogleFacade.SCOPE;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class GoogleFacadeTest {

    @Mock
    private GoogleFeignClient googleClient;

    private GoogleFacade googleFacade;

    private final static String QUERY_PARAMS = "?client_id=clientId&redirect_uri=redirectUri&response_type=code&access_type=offline&prompt=consent";

    @BeforeEach
    void setUp() {
        googleFacade = new GoogleFacade(googleClient);
    }

    @Test
    void getOAuthAuthorizationFlowURI_ValidURI() {
        // GIVEN
        String state = "state";

        // WHEN
        URI uri = googleFacade.getOAuthAuthorizationFlowURI(state);

        // THEN
        assertNotNull(uri);
        assertEquals(AUTH_URL + QUERY_PARAMS + "&scope=" + encode(SCOPE, UTF_8) + "&state=" + state, uri.toString());
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
        RefreshableAuthorization authorization = googleFacade.exchangeOAuthToken(ACCESS_TOKEN);

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
        RefreshableAuthorization authorization = googleFacade.refreshOAuthToken(REFRESH_TOKEN);

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
