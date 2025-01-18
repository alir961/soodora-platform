package com.grooveconductor.api.oauth;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.auth.AuthenticationException;
import com.grooveconductor.service.google.GoogleFacade;
import com.grooveconductor.service.oauth.OAuthTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.URISyntaxException;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static org.springframework.http.HttpStatus.FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
@Tag(name = "Google OAuth 2.0 Controller")
public class GoogleOAuthController {

    private final OAuthTokenService oauthTokenService;

    private final GoogleFacade googleFacade;

    @Value("${frontURL:localhost}")
    private String frontUrl;

    @GetMapping
    @ResponseStatus(FOUND)
    public HttpHeaders redirectToGoogleOAuthFlow(@RequestParam String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(oauthTokenService.retrieveUserOAuthURI(token));
        return headers;
    }

    @ResponseStatus(FOUND)
    @GetMapping("/callback")
    public HttpHeaders googleOAuthCallback(@RequestParam String code, @RequestParam String state) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        String url = frontUrl + "/account";
        UserDTO user = null;

        try {
            user = oauthTokenService.retrieveUserOrThrows(state);
            oauthTokenService.updateUserAuthorization(GOOGLE, user, googleFacade.exchangeOAuthToken(code));
        } catch (AuthenticationException | HttpClientErrorException ignored) {
            if (user != null)
                oauthTokenService.deleteUserAuthorization(GOOGLE, user);
            url += "?error=oauth";
        }
        headers.setLocation(new URI(url));
        return headers;
    }

    @DeleteMapping
    public void deleteGoogleOAuth(@AuthenticationPrincipal UserDTO user) {
        oauthTokenService.deleteUserAuthorization(GOOGLE, user);
    }

}
