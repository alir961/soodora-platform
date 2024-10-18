package com.grooveconductor.rest.oauth;

import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.google.GoogleService;
import com.grooveconductor.application.oauth.OAuthTokenService;
import com.grooveconductor.application.user.UserService;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
public class GoogleOAuthController {

    private final GoogleService googleService;
    private final UserService userService;
    private final OAuthTokenService oAuthTokenService;
    private final JwtTokenService jwtTokenService;

    @GetMapping
    public ResponseEntity<HttpHeaders> redirectToGoogleOAuthFlow(@RequestParam String token) {
        HttpHeaders headers = new HttpHeaders();

        if(!userService.existsUser(getIdentifierFromJWT(token)))
            return new ResponseEntity<>(UNAUTHORIZED);
        headers.setLocation(googleService.getOAuthAuthorizationFlowURI(token));
        return new ResponseEntity<>(headers, FOUND);
    }

    @GetMapping("/callback")
    public void googleOAuthCallback(@RequestParam String code, @RequestParam String state) {
        UserDTO user = userService.retrieveUser(getIdentifierFromJWT(state));
        RefreshableAuthorization authorization = googleService.callExchangeTokenAPI(code);

        oAuthTokenService.updateUserAuthorization(GOOGLE, user, authorization);
    }

    private Identifier getIdentifierFromJWT(String token) {
        String email = jwtTokenService.getEmailFromToken(token);
        UserDTO user = userService.retrieveUser(createFromEmail(email));

        return user.identifier();
    }

}
