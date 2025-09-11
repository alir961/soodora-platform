package com.soodora.infrastructure.oauth;

import com.soodora.infrastructure.user.UserEntity;

import static java.util.UUID.randomUUID;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;

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
