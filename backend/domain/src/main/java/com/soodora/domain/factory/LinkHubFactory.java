package com.soodora.domain.factory;

import com.soodora.domain.link.Link;
import com.soodora.domain.linkhub.LinkHub;

import java.util.Date;
import java.util.List;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.factory.UserFactory.buildDefaultArtistUser;

@NoArgsConstructor(access = PROTECTED)
public class LinkHubFactory {

    public static LinkHub buildDefaultLinkHub(String slug, String name, List<Link> links) {
        return new LinkHub(
                slug,
                name,
                "Description",
                buildDefaultArtistUser(),
                links,
                new Date(),
                new Date(),
                0);
    }

}
