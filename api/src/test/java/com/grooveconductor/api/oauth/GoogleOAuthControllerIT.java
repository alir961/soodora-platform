package com.grooveconductor.api.oauth;

import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.infrastructure.auth.JwtTokenRepository;
import com.grooveconductor.service.auth.AuthenticationException;
import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.google.GoogleFacade;
import com.grooveconductor.service.oauth.OAuthTokenService;
import com.grooveconductor.service.user.UserService;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class GoogleOAuthControllerIT {

    private static final String INVALID_TOKEN = "invalid";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleFacade googleFacade;

    @MockBean
    private UserService userService;

    @MockBean
    private OAuthTokenService oauthTokenService;

    @MockBean
    private JwtTokenRepository jwtTokenService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(jwtTokenService.getSubjectFromToken(JWT_TOKEN)).thenReturn(USER.email());
        when(userService.retrieveUser(createFromEmail(USER.email()))).thenReturn(USER);
    }

    @Test
    void redirectToGoogleOAuthFlow_Redirect() throws Exception {
        // GIVEN
        when(oauthTokenService.retrieveUserGoogleOAuthURI(JWT_TOKEN)).thenReturn(new URI(REDIRECT_URL));

        // WHEN & THEN
        mockMvc.perform(get("/auth/google")
                .param("token", JWT_TOKEN))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(REDIRECT_URL)));
    }

    @Test
    void redirectToGoogleOAuthFlow_Unauthorized() throws Exception {
        // GIVEN
        when(oauthTokenService.retrieveUserGoogleOAuthURI(INVALID_TOKEN))
                .thenThrow(AuthenticationException.class);

        // WHEN & THEN
        mockMvc.perform(get("/auth/google")
                .param("token", INVALID_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void googleOAuthCallback_Success() throws Exception {
        // GIVEN
        String code = "code";

        when(oauthTokenService.processGoogleAuthentication(code, JWT_TOKEN)).thenReturn("url");
        when(oauthTokenService.retrieveUserOrThrows(JWT_TOKEN)).thenReturn(USER);
        when(googleFacade.exchangeOAuthToken("code")).thenReturn(AUTHORIZATION);

        // WHEN & THEN
        mockMvc.perform(get("/auth/google/callback")
                .param("code", code)
                .param("state", JWT_TOKEN)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isFound());
    }

    @Test
    void deleteGoogleOAuth_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        when(userService.existsUser(USER.identifier())).thenReturn(true);
        when(googleFacade.getOAuthAuthorizationFlowURI(JWT_TOKEN)).thenReturn(new URI(REDIRECT_URL));

        // WHEN & THEN
        mockMvc.perform(delete("/auth/google")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(oauthTokenService)
                .deleteUserAuthorization(GOOGLE, USER);
    }

}
