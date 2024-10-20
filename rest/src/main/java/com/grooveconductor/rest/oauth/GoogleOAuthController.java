package com.grooveconductor.rest.oauth;

import com.grooveconductor.application.auth.AuthenticationException;
import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.google.GoogleService;
import com.grooveconductor.application.oauth.OAuthTokenService;
import com.grooveconductor.application.user.UserService;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.UserDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
@Tag(name = "Google OAuth 2.0 Controller")
public class GoogleOAuthController {

    private final GoogleService googleService;
    private final UserService userService;
    private final OAuthTokenService oauthTokenService;
    private final JwtTokenService jwtTokenService;

    @GetMapping
    @ResponseStatus(FOUND)
    public HttpHeaders redirectToGoogleOAuthFlow(@RequestParam String token) {
        HttpHeaders headers = new HttpHeaders();

        retrieveUserOrThrows(token);
        headers.setLocation(googleService.getOAuthAuthorizationFlowURI(token));
        return headers;
    }

    @GetMapping("/callback")
    public void googleOAuthCallback(@RequestParam String code, @RequestParam String state) {
        UserDTO user = retrieveUserOrThrows(state);
        RefreshableAuthorization authorization = googleService.exchangeOAuthToken(code);

        oauthTokenService.updateUserAuthorization(GOOGLE, user, authorization);
    }

    private UserDTO retrieveUserOrThrows(String token) throws AuthenticationException {
        if (token == null || token.isEmpty()) throw new AuthenticationException("Token is null or empty");
        String email = jwtTokenService.getEmailFromToken(token);
        UserDTO user = userService.retrieveUser(createFromEmail(email));

        if (email == null || user == null) throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

}
