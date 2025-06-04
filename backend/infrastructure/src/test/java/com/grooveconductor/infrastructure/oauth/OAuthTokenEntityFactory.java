package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.infrastructure.user.UserEntity;

import static java.util.UUID.randomUUID;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;

public class OAuthTokenEntityFactory {

    public static OAuthTokenEntity buildDefaultOAuthTokenEntity(UserEntity user) {
        return new OAuthTokenEntity(
                randomUUID(),
                GOOGLE,
                "external-id",
                "refresh-externalToken",
                user);
    }

}
