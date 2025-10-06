package com.soodora.infrastructure.linkpage;

import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.link.LinkEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LinkPageEntityFactory {

    public LinkPageEntity buildDefaultLinkPageEntity(ArtistEntity artist) {
        List<LinkEntity> links = new ArrayList<>();

        LinkPageEntity linkPageEntity = new LinkPageEntity(
                UUID.randomUUID(),
                "slug",
                "Post 1",
                "Content",
                artist,
                new ArrayList<>(),
                new Date(),
                new Date());

        LinkEntity link = new LinkEntity(
                UUID.randomUUID(),
                "slug",
                "title",
                "url",
                "iconUrl",
                "description",
                linkPageEntity,
                new Date(),
                new Date());

        links.add(link);
        linkPageEntity.setLinks(links);

        return linkPageEntity;
    }
}
