package com.grooveconductor.rest.oauth;

import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.google.GoogleService;
import com.grooveconductor.application.oauth.OAuthTokenService;
import com.grooveconductor.application.user.UserService;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GoogleOAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleService googleService;

    @MockBean
    private UserService userService;

    @MockBean
    private OAuthTokenService oauthTokenService;

    @MockBean
    private JwtTokenService jwtTokenService;

    private static final String JWT_TOKEN = "token";
    private static final UserDTO USER = buildDefaultUser();
    private static final String REDIRECT_URL = "http://localhost:8080/";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final RefreshableAuthorization AUTHORIZATION = new RefreshableAuthorization(ACCESS_TOKEN, REFRESH_TOKEN, 60);

    @BeforeEach
    void setUp() {
        when(jwtTokenService.getEmailFromToken(JWT_TOKEN)).thenReturn(USER.email());
        when(userService.retrieveUser(createFromEmail(USER.email()))).thenReturn(USER);
    }

    @Test
    void redirectToGoogleOAuthFlow_Redirect() throws Exception {
        // GIVEN
        when(userService.existsUser(USER.identifier())).thenReturn(true);
        when(googleService.getOAuthAuthorizationFlowURI(JWT_TOKEN)).thenReturn(new URI(REDIRECT_URL));

        // WHEN & THEN
        mockMvc.perform(get("/auth/google")
                .param("token", JWT_TOKEN))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", is(REDIRECT_URL)));
    }

    @Test
    void redirectToGoogleOAuthFlow_Unauthorized() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/auth/google")
                .param("token", "invalid"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void googleOAuthCallback_Success() throws Exception {
        // GIVEN
        when(googleService.exchangeOAuthToken("code")).thenReturn(AUTHORIZATION);
        doNothing().when(oauthTokenService).updateUserAuthorization(GOOGLE, USER, AUTHORIZATION);

        // WHEN & THEN
        mockMvc.perform(get("/auth/google/callback")
                .param("code", "code")
                .param("state", JWT_TOKEN))
            .andExpect(status().isOk());
        verify(oauthTokenService, times(1)).updateUserAuthorization(GOOGLE, USER, AUTHORIZATION);
    }

    @Test
    void googleOAuthCallback_Unauthorized() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/auth/google/callback")
                .param("code", "code")
                .param("state", "invalid"))
            .andExpect(status().isUnauthorized());
    }

}