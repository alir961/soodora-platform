package com.soodora.domain.oauth;

public record RefreshableAuthorization(String externalToken, String accessToken, String refreshToken, int expiresIn) {
}
