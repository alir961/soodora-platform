package com.grooveconductor.rest.oauth;

import com.grooveconductor.application.auth.AuthenticationException;
import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.google.GoogleService;
import com.grooveconductor.application.oauth.OAuthTokenService;
import com.grooveconductor.application.user.UserService;
import com.grooveconductor.domain.user.UserDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.URISyntaxException;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static org.springframework.http.HttpStatus.FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
@Tag(name = "Google OAuth 2.0 Controller")
public class GoogleOAuthController {

    private final GoogleService googleService;
    private final UserService userService;
    private final OAuthTokenService oauthTokenService;
    private final JwtTokenService jwtTokenService;

    @Value("${frontURL:localhost}")
    private String frontUrl;

    @GetMapping
    @ResponseStatus(FOUND)
    public HttpHeaders redirectToGoogleOAuthFlow(@RequestParam String token) {
        HttpHeaders headers = new HttpHeaders();

        retrieveUserOrThrows(token);
        headers.setLocation(googleService.getOAuthAuthorizationFlowURI(token));
        return headers;
    }

    @ResponseStatus(FOUND)
    @GetMapping("/callback")
    public HttpHeaders googleOAuthCallback(@RequestParam String code, @RequestParam String state) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        String url = frontUrl + "/account";
        UserDTO user = null;

        try {
            user = retrieveUserOrThrows(state);
            oauthTokenService.updateUserAuthorization(GOOGLE, user, googleService.exchangeOAuthToken(code));
        } catch (AuthenticationException | HttpClientErrorException ignored) {
            if (user != null)
                oauthTokenService.deleteUserAuthorization(GOOGLE, user);
            url += "?error=oauth";
        }
        headers.setLocation(new URI(url));
        return headers;
    }

    private UserDTO retrieveUserOrThrows(String token) throws AuthenticationException {
        if (token == null || token.isEmpty()) throw new AuthenticationException("Token is null or empty");
        String email = jwtTokenService.getEmailFromToken(token);
        UserDTO user = userService.retrieveUser(createFromEmail(email));

        if (email == null || user == null) throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

}
