package com.soodora.api.oauth;

import com.soodora.api.auth.LoginResponse;
import com.soodora.domain.auth.AuthenticationTokenException;
import com.soodora.domain.oauth.OAuthAuthorizationException;
import com.soodora.domain.user.User;
import com.soodora.service.oauth.OAuthTokenService;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.FOUND;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
@Tag(name = "Google OAuth 2.0 Controller")
public class GoogleOAuthController {

    private final OAuthTokenService oauthTokenService;

    @Value("${application.url:localhost}")
    private String frontUrl;

    /** Sign in with Google. */
    @GetMapping
    @ResponseStatus(FOUND)
    public HttpHeaders redirectToGoogleOAuthFlow() {
        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(oauthTokenService.retrieveGogoleUnauthorizedOAuthURI());
        return headers;
    }

    /**
     * Link Google to the signed-in account. The session token travels in the
     * Authorization header, never in a URL; the browser is then sent to the
     * returned Google URL.
     */
    @PostMapping("/link")
    public OAuthLinkResponse linkGoogleAccount(@AuthenticationPrincipal User user) {
        return new OAuthLinkResponse(oauthTokenService.retrieveUserGoogleOAuthURI(user).toString());
    }

    /** Trades the one-time code from the sign-in redirect for a session token. */
    @PostMapping("/token")
    public LoginResponse exchangeLoginCode(@RequestBody OAuthCodeRequest request) {
        return new LoginResponse(oauthTokenService.exchangeLoginCode(request.code()));
    }

    @ResponseStatus(FOUND)
    @GetMapping("/callback")
    public HttpHeaders googleOAuthCallback(@RequestParam String code, @RequestParam String state)
            throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();

        try {
            String url = oauthTokenService.processGoogleAuthentication(code, state);

            headers.setLocation(new URI(url));
        } catch (AuthenticationTokenException | OAuthAuthorizationException ignored) {
            headers.setLocation(new URI(frontUrl + "/account?error=oauth"));
        }
        return headers;
    }

    @DeleteMapping
    public void deleteGoogleOAuth(@AuthenticationPrincipal User user) {
        oauthTokenService.deleteUserAuthorization(GOOGLE, user);
    }

}
