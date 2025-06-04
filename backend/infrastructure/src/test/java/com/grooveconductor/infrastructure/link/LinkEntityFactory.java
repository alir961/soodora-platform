package com.grooveconductor.infrastructure.link;

import com.grooveconductor.infrastructure.linkpage.LinkPageEntity;

import java.util.Date;
import java.util.UUID;

public class LinkEntityFactory {

    public static LinkEntity buildDefaultLinkEntity(LinkPageEntity linkPageEntity) {
        return new LinkEntity(
                UUID.randomUUID(),
                "slug",
                "title",
                "url",
                "url",
                "description",
                linkPageEntity,
                new Date(),
                new Date());
    }

}
