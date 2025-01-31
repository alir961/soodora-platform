package com.grooveconductor.service.google;

import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.service.oauth.OAuthFlowException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static com.grooveconductor.service.google.GoogleFacade.TOKEN_URL;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.client.RestClient.create;

@Component
public class GoogleFeignClient {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    public RefreshableAuthorization callTokenAPI(Map<String, String> body) {
        return parseResponse(
                create()
                        .post()
                        .uri(TOKEN_URL)
                        .headers(consumer -> consumer.setContentType(APPLICATION_FORM_URLENCODED))
                        .body(encodeFormData(body))
                        .retrieve()
                        .body(Map.class),
                body.getOrDefault(REFRESH_TOKEN, null));
    }

    private RefreshableAuthorization parseResponse(Map response, String refreshToken) throws OAuthFlowException {
        try {
            Map<String, Object> body = throwIfInvalidResponse(response, refreshToken);

            return new RefreshableAuthorization(
                    body.get(ACCESS_TOKEN).toString(),
                    body.getOrDefault(REFRESH_TOKEN, refreshToken).toString(),
                    (Integer) body.get("expires_in"));
        } catch (HttpClientErrorException e) {
            throw new OAuthFlowException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new OAuthFlowException(e.getMessage());
        }
    }

    private static Map<String, Object> throwIfInvalidResponse(Map<String, Object> body, String refreshToken) {
        if (body == null)
            throw new OAuthFlowException("Invalid Google server response");
        if (!body.containsKey(ACCESS_TOKEN))
            throw new OAuthFlowException("Missing access token");
        if (body.getOrDefault(REFRESH_TOKEN, refreshToken) == null)
            throw new OAuthFlowException("Missing refresh token");
        return body;
    }

    private String encodeFormData(Map<String, String> data) {
        return data.entrySet()
                .stream()
                .map(entry -> encode(entry.getKey(), UTF_8) + "=" +
                        encode(entry.getValue(), UTF_8))
                .collect(joining("&"));
    }

}
