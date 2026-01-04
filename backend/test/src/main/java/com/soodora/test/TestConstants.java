package com.soodora.test;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.event.Event;
import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.oauth.RefreshableAuthorization;
import com.soodora.domain.product.Product;
import com.soodora.domain.sale.Sale;
import com.soodora.domain.user.User;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import static com.soodora.domain.factory.MediaKitFactory.buildDefaultMediaKit;
import static com.soodora.domain.factory.MediaKitItemFactory.buildDefaultMediaKitItem;
import static com.soodora.domain.factory.ProductFactory.buildDefaultProduct;
import static com.soodora.domain.factory.SaleFactory.buildDefaultSale;
import static com.soodora.domain.factory.UserFactory.*;
import static com.soodora.test.factory.ArtistFactory.buildDefaultArtist;
import static com.soodora.test.factory.EventFactory.buildDefaultEvent;

@NoArgsConstructor(access = PRIVATE)
public class TestConstants {

    public static final String EXTERNAL_TOKEN = "external-token";
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final RefreshableAuthorization AUTHORIZATION = new RefreshableAuthorization(
            EXTERNAL_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN, 60);
    public static final String VALID_PASSWORD = "Password1234!";
    public static final String JWT_TOKEN = "externalToken";

    public static final String REDIRECT_URL = "http://localhost:8080/callback";

    public static final User USER = buildDefaultUser();
    public static final User ADMIN = buildDefaultAdminUser();
    public static final User ARTIST_USER = buildDefaultArtistUser();
    public static final ArtistDTO ARTIST = buildDefaultArtist("");
    public static final Event EVENT = buildDefaultEvent();
    public static final MediaKit MEDIA_KIT = buildDefaultMediaKit();
    public static final MediaKitItem MEDIA_KIT_ITEM = buildDefaultMediaKitItem();
    public static final Sale SALE = buildDefaultSale();
    public static final Product PRODUCT = buildDefaultProduct();

}
