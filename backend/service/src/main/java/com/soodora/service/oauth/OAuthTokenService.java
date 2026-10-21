package com.soodora.service.oauth;

import com.soodora.domain.auth.AuthenticationTokenException;
import com.soodora.domain.auth.TokenRepository;
import com.soodora.domain.oauth.*;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.AuthenticationException;
import com.soodora.service.google.GoogleFacade;

import java.net.URI;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;
import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;
import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthTokenService {

    @Value("${application.url:localhost}")
    private String frontUrl;

    private final OAuthTokenRepository oauthRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final AuthorizationMapper authorizationMapper;

    private final GoogleFacade googleFacade;

    // How long the browser has to come back from Google, and to exchange the login
    // code.
    private static final Duration STATE_TTL = Duration.ofMinutes(10);
    private static final Duration LOGIN_CODE_TTL = Duration.ofMinutes(2);
    // Refresh Google access tokens a little before they actually expire.
    private static final Duration EXPIRY_MARGIN = Duration.ofMinutes(1);
    private static final SecureRandom RANDOM = new SecureRandom();

    private Clock clock = Clock.systemUTC();

    // OAuth state -> who started the flow (a null user slug means a sign-in,
    // otherwise account linking)
    private final Map<String, Pending> pendingStates = new ConcurrentHashMap<>();
    // One-time code given to the browser after a Google sign-in -> the session
    // token it stands for
    private final Map<String, Pending> loginCodes = new ConcurrentHashMap<>();
    private final Map<String, CachedAccessToken> userAccessTokens = new ConcurrentHashMap<>();

    private record Pending(String value, Instant expiresAt) {
    }

    private record CachedAccessToken(String token, Instant expiresAt) {
    }

    public String processGoogleAuthentication(String code, String state) {
        String baseUrl = frontUrl + "/authentication";
        Pending pending = state == null ? null : pendingStates.remove(state);

        // Unknown, reused or expired state: the callback did not come from a flow we
        // started.
        if (pending == null || isExpired(pending))
            return baseUrl + "?error=oauth";

        RefreshableAuthorization authorization = googleFacade.exchangeOAuthToken(code);
        String externalId = tokenRepository.getSubjectFromGoogleToken(authorization.externalToken());

        if (pending.value() == null)
            return processLogin(authorization, externalId, baseUrl);
        try {
            User user = ofNullableUser(pending.value());

            try {
                updateGoogleUserAuthorization(user, externalId, authorization);
            } catch (HttpClientErrorException | AuthenticationTokenException ignored) {
                deleteUserAuthorization(GOOGLE, user);
                return baseUrl + "?error=oauth";
            }
        } catch (AuthenticationException | OAuthAuthorizationException ignored) {
            return baseUrl + "?error=oauth";
        }
        return baseUrl;
    }

    /** Starts a Google sign-in. */
    public URI retrieveGogoleUnauthorizedOAuthURI() {
        return googleFacade.getOAuthAuthorizationFlowURI(newState(null));
    }

    /** Starts linking Google to the signed-in user's account. */
    public URI retrieveUserGoogleOAuthURI(User user) {
        if (user == null)
            throw new AuthenticationException("You must be signed in to link an account");
        return googleFacade.getOAuthAuthorizationFlowURI(newState(user.getSlug()));
    }

    /** Trades the one-time code from the sign-in redirect for a session token. */
    public String exchangeLoginCode(String code) {
        Pending pending = code == null ? null : loginCodes.remove(code);

        if (pending == null || isExpired(pending))
            throw new AuthenticationException("Unknown or expired login code");
        return pending.value();
    }

    public String retrieveUserAccessToken(OAuthProvider provider, User user) {
        CachedAccessToken cached = userAccessTokens.get(user.getSlug());

        if (cached != null && clock.instant().isBefore(cached.expiresAt()))
            return cached.token();
        String refreshToken = retrieveUserRefreshToken(provider, user);
        RefreshableAuthorization authorization = googleFacade.refreshOAuthToken(refreshToken);

        cacheAccessToken(user, authorization);
        return authorization.accessToken();
    }

    public String retrieveUserRefreshToken(OAuthProvider provider, User user) {
        if (oauthRepository.hasAuthorization(provider, user))
            return oauthRepository.retrieveRefreshToken(provider, user);
        throw new OAuthFlowException(
                "Unable to retrieve user #" + user.getSlug() + " refresh externalToken for " + provider);
    }

    public User retrieveUserOrThrows(String token) throws AuthenticationException {
        if (token == null || token.isEmpty())
            throw new AuthenticationException("Token is null or empty");
        String email = tokenRepository.getSubjectFromToken(token);
        User user = userRepository.find(createFromEmail(email));

        if (email == null || user == null)
            throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

    public void deleteUserAuthorization(OAuthProvider provider, User user) {
        oauthRepository.deleteAuthorization(provider, user);
    }

    private String processLogin(RefreshableAuthorization authorization, String externalId, String baseUrl) {
        User user;

        try {
            user = oauthRepository.retrieveUser(GOOGLE, externalId);
        } catch (OAuthAuthorizationException ignored) {
            user = null;
        }
        if (user == null)
            return baseUrl + "?external=" + authorizationMapper.registerAuthorization(authorization);
        // The session token never appears in a URL: the browser gets a short-lived
        // one-time code.
        String loginCode = randomToken();

        loginCodes.put(loginCode,
                new Pending(tokenRepository.generateToken(user), clock.instant().plus(LOGIN_CODE_TTL)));
        return baseUrl + "?code=" + loginCode;
    }

    private User ofNullableUser(String slug) {
        User user = userRepository.find(createFromSlug(slug));

        if (user == null)
            throw new AuthenticationException("Unable to retrieve user");
        return user;
    }

    private void updateGoogleUserAuthorization(User user, String externalId, RefreshableAuthorization authorization) {
        if (oauthRepository.hasAuthorization(GOOGLE, user))
            oauthRepository.deleteAuthorization(GOOGLE, user);
        cacheAccessToken(user, authorization);
        oauthRepository.createAuthorization(GOOGLE, user, externalId, authorization.refreshToken());
    }

    private void cacheAccessToken(User user, RefreshableAuthorization authorization) {
        Instant expiresAt = clock.instant().plusSeconds(authorization.expiresIn()).minus(EXPIRY_MARGIN);

        userAccessTokens.put(user.getSlug(), new CachedAccessToken(authorization.accessToken(), expiresAt));
    }

    private String newState(String userSlug) {
        String state = randomToken();

        removeExpired();
        pendingStates.put(state, new Pending(userSlug, clock.instant().plus(STATE_TTL)));
        return state;
    }

    // Flows that are never finished must not pile up in memory.
    private void removeExpired() {
        pendingStates.values().removeIf(this::isExpired);
        loginCodes.values().removeIf(this::isExpired);
    }

    private boolean isExpired(Pending pending) {
        return !clock.instant().isBefore(pending.expiresAt());
    }

    private static String randomToken() {
        byte[] bytes = new byte[32];

        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    void setClock(Clock clock) {
        this.clock = clock;
    }

}
