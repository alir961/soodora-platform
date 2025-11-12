package com.soodora.api.oauth;

import com.soodora.domain.user.UserRepository;
import com.soodora.infrastructure.auth.JwtTokenRepository;
import com.soodora.service.auth.AuthenticationException;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.google.GoogleFacade;
import com.soodora.service.oauth.OAuthTokenService;
import com.soodora.service.user.UserService;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;
import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class GoogleOAuthControllerIT {

    private static final String INVALID_TOKEN = "invalid";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoogleFacade googleFacade;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OAuthTokenService oauthTokenService;

    @MockitoBean
    private JwtTokenRepository jwtTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(jwtTokenService.getSubjectFromToken(JWT_TOKEN)).thenReturn(USER.getEmail().value());
        when(userService.retrieveUser(createFromEmail(USER.getEmail().value()))).thenReturn(USER);
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
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        when(userService.retrieveUser(USER.getIdentifier())).thenReturn(USER);
        when(googleFacade.getOAuthAuthorizationFlowURI(JWT_TOKEN)).thenReturn(new URI(REDIRECT_URL));

        // WHEN & THEN
        mockMvc.perform(delete("/auth/google")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(oauthTokenService)
                .deleteUserAuthorization(GOOGLE, USER);
    }

}
