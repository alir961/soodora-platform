package com.soodora.service.google;

import com.soodora.domain.oauth.OAuthAuthorizationException;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.service.oauth.OAuthFacade;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class GoogleFacade implements OAuthFacade {

    @Value("${oauth.google.client-id}")
    private String clientId = "clientId";

    @Value("${oauth.google.client-secret}")
    private String clientSecret = "clientSecret";

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri = "redirectUri";

    private final GoogleFeignClient client;

    static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/youtube.readonly";
    static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Override
    public URI getOAuthAuthorizationFlowURI(String state) {
        String url = AUTH_URL + "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&access_type=offline" +
                "&prompt=consent" +
                "&scope=" + encode(SCOPE, UTF_8) +
                "&state=" + state;

        return URI.create(url);
    }

    @Override
    public RefreshableAuthorization exchangeOAuthToken(String code) throws HttpClientErrorException {
        Map<String, String> body = new HashMap<>();

        body.put("code", code);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("redirect_uri", redirectUri);
        body.put("grant_type", "authorization_code");

        try {
            return client.callTokenAPI(body);
        } catch (HttpClientErrorException ignored) {
            throw new OAuthAuthorizationException("Unable to exchange oauth token");
        }
    }

    @Override
    public RefreshableAuthorization refreshOAuthToken(String refreshToken) throws HttpClientErrorException {
        Map<String, String> body = new HashMap<>();

        body.put("refresh_token", refreshToken);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("grant_type", "refresh_token");
        return client.callTokenAPI(body);
    }

}
