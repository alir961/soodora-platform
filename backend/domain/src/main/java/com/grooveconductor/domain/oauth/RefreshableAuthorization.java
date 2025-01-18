package com.grooveconductor.domain.oauth;

public record RefreshableAuthorization(String accessToken, String refreshToken, int expiresIn) {
}
