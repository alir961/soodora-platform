package com.grooveconductor.domain.oauth;

public record RefreshableAuthorization(String externalToken, String accessToken, String refreshToken, int expiresIn) {
}
