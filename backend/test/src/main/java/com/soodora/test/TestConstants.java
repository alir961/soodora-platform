package com.soodora.test;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.event.EventDTO;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.domain.user.User;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static com.soodora.test.factory.ArtistFactory.buildArtistDTO;
import static com.soodora.test.factory.EventFactory.buildEventDTO;
import static com.soodora.test.factory.UserFactory.*;

@NoArgsConstructor(access = PRIVATE)
public class TestConstants {

    public static final String EXTERNAL_ID = "external-id";
    public static final String EXTERNAL_TOKEN = "external-token";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final RefreshableAuthorization AUTHORIZATION = new RefreshableAuthorization(
            EXTERNAL_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN, 60);
    public static final String VALID_PASSWORD = "Password1234!";
    public static final User USER = buildDefaultUser();
    public static final User ADMIN = buildDefaultAdminUser();
    public static final User ARTIST_USER = buildDefaultArtistUser();
    public static final ArtistDTO ARTIST = buildArtistDTO("");
    public static final EventDTO EVENT = buildEventDTO("");
    public static final String JWT_TOKEN = "externalToken";
    public static final String REDIRECT_URL = "http://localhost:8080/callback";

}
