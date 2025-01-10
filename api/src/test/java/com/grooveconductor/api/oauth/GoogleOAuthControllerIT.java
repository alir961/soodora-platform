package com.grooveconductor.api.oauth;

import com.grooveconductor.service.auth.AuthenticationService;
import com.grooveconductor.service.auth.JwtTokenService;
import com.grooveconductor.service.google.GoogleService;
import com.grooveconductor.service.oauth.OAuthTokenService;
import com.grooveconductor.service.user.UserService;
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
import static com.grooveconductor.test.TestConstants.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GoogleOAuthControllerIT {

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

    @MockBean
    private AuthenticationService authenticationService;

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
            .andExpect(status().isFound())
            .andExpect(header().string("Location", not(containsString("error=oauth"))));
        verify(oauthTokenService, times(1)).updateUserAuthorization(GOOGLE, USER, AUTHORIZATION);
    }

    @Test
    void googleOAuthCallback_Unauthorized() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/auth/google/callback")
                .param("code", "code")
                .param("state", "invalid"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", containsString("error=oauth")));
    }

    @Test
    void deleteGoogleOAuth_Success() throws Exception {
        // GIVEN
        when(authenticationService.getAuthenticatedUser()).thenReturn(USER);
        when(userService.existsUser(USER.identifier())).thenReturn(true);
        when(googleService.getOAuthAuthorizationFlowURI(JWT_TOKEN)).thenReturn(new URI(REDIRECT_URL));

        // WHEN & THEN
        mockMvc.perform(delete("/auth/google")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
        verify(oauthTokenService, times(1)).deleteUserAuthorization(GOOGLE, USER);
    }

}