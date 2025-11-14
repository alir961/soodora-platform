package com.soodora.test.factory;

import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkhub.LinkHubDTO;

import java.util.List;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.factory.ArtistFactory.buildDefaultArtist;

@NoArgsConstructor(access = PROTECTED)
public class LinkHubFactory {

    public static LinkHubDTO buildDefaultLinkHub(String slug, String name, List<LinkDTO> links) {
        return new LinkHubDTO(
                slug,
                name,
                "Description",
                links,
                buildDefaultArtist("id"));
    }

}
