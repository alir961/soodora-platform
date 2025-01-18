package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.infrastructure.user.UserEntity;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static java.util.UUID.randomUUID;

public class OAuthTokenEntityFactory {

    public static OAuthTokenEntity buildDefaultOAuthTokenEntity(UserEntity user) {
        return new OAuthTokenEntity(
            randomUUID(),
            GOOGLE,
            "refresh-token",
            user
        );
    }

}
