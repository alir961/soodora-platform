package com.soodora.infrastructure.post;

import com.soodora.domain.factory.PostFactory;

import java.util.Date;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;

import static com.soodora.infrastructure.artist.ArtistEntityFactory.buildDefaultArtistEntity;
import static com.soodora.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;

public class PostEntityFactory extends PostFactory {

    public static PostEntity buildDefaultPostEntity() {
        return new PostEntity(
                randomUUID(),
                "slug",
                "Post 1",
                "Content",
                "imageUrl",
                "My audio",
                "audioUrl",
                buildDefaultArtistEntity(),
                emptySet(),
                emptySet(),
                emptySet(),
                emptySet(),
                new Date(),
                new Date());
    }

    public static CommentEntity buildDefaultCommentEntity() {
        return new CommentEntity(
                randomUUID(),
                "slug",
                "Content",
                buildDefaultPostEntity(),
                buildDefaultUserEntity(),
                emptyList(),
                new Date());
    }

    public static LikeEntity buildDefaultPostLikeEntity() {
        return new LikeEntity(
                randomUUID(),
                "slug",
                buildDefaultPostEntity(),
                null,
                buildDefaultUserEntity(),
                new Date());
    }

}
