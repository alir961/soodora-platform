package com.grooveconductor.test.factory;

import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.domain.linkpage.LinkPageDTO;

import java.util.List;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;

@NoArgsConstructor(access = PROTECTED)
public class LinkPageFactory {

    public static LinkPageDTO buildDefaultLinkPage(String slug, String name, List<LinkDTO> links) {
        return new LinkPageDTO(
                slug,
                name,
                "Description",
                links,
                buildDefaultUser());
    }

}
