package com.grooveconductor.test.factory;

import com.grooveconductor.domain.link.LinkDTO;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class LinkFactory {

    public static LinkDTO buildDefaultLink(String slug, String name) {
        return new LinkDTO(
                slug,
                name,
                "Description",
                "url",
                "iconUrl");
    }

}
