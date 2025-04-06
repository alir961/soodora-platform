package com.grooveconductor.test;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import com.grooveconductor.domain.user.UserDTO;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static com.grooveconductor.test.factory.ArtistFactory.buildArtistDTO;
import static com.grooveconductor.test.factory.EventFactory.buildEventDTO;
import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;

@NoArgsConstructor(access = PRIVATE)
public class TestConstants {

    public static final String EXTERNAL_ID = "external-id";
    public static final String EXTERNAL_TOKEN = "external-token";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final RefreshableAuthorization AUTHORIZATION = new RefreshableAuthorization(
            EXTERNAL_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN, 60);
    public static final UserDTO USER = buildDefaultUser();
    public static final ArtistDTO ARTIST = buildArtistDTO("");
    public static final EventDTO EVENT = buildEventDTO("");
    public static final String JWT_TOKEN = "externalToken";
    public static final String REDIRECT_URL = "http://localhost:8080/callback";

}
