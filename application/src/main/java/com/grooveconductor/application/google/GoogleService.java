package com.grooveconductor.application.google;

import com.grooveconductor.application.oauth.OAuthFlowException;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@RequiredArgsConstructor
public class GoogleService {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private static final String SCOPE = "https://www.googleapis.com/auth/youtube.readonly";
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    public URI getOAuthAuthorizationFlowURI(String state) {
        String uriBuilder = AUTH_URL + "?client_id=" + clientId +
            "&redirect_uri=" + redirectUri +
            "&response_type=code" +
            "&access_type=offline" +
            "&scope=" + SCOPE +
            "&state=" + state;

        return URI.create(uriBuilder);
    }

    public RefreshableAuthorization callExchangeTokenAPI(String code) throws HttpClientErrorException {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");
        return callTokenAPI(body);
    }

    public RefreshableAuthorization callRefreshTokenAPI(String refreshToken) throws HttpClientErrorException {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("refresh_token", refreshToken);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "refresh_token");
        return callTokenAPI(body);
    }

    private RefreshableAuthorization parseResponse(ResponseEntity<Map> response, String refreshToken) throws OAuthFlowException {
        try {
            Map<String, Object> body = response.getBody();

            if (response.getStatusCode() != OK) throw new OAuthFlowException("Invalid status code from Google server");
            if (body == null) throw new OAuthFlowException("Invalid Google server response");
            if (!body.containsKey("access_token")) throw new OAuthFlowException("Missing access token");
            if (refreshToken == null && !body.containsKey("refresh_token")) throw new OAuthFlowException("Missing refresh token");
            return new RefreshableAuthorization(
                body.get("access_token").toString(),
                refreshToken == null ? body.get("refresh_token").toString() : refreshToken,
                (Integer) body.get("expires_in")
            );
        } catch (HttpClientErrorException e) {
            throw new OAuthFlowException(e.getResponseBodyAsString());
        }
    }

    private RefreshableAuthorization callTokenAPI(MultiValueMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        headers.setContentType(APPLICATION_FORM_URLENCODED);
        return parseResponse(restTemplate.exchange(
            TOKEN_URL,
            POST,
            new HttpEntity<>(body, headers),
            Map.class
        ), body.get("refresh_token").toString());
    }

}
