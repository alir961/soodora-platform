package com.grooveconductor.test;

import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.UserDTO;
import lombok.NoArgsConstructor;

import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestConstants {

    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final RefreshableAuthorization AUTHORIZATION = new RefreshableAuthorization(ACCESS_TOKEN, REFRESH_TOKEN, 60);
    public static final UserDTO USER = buildDefaultUser();
    public static final String JWT_TOKEN = "token";
    public static final String REDIRECT_URL = "http://localhost:8080/callback";

}
