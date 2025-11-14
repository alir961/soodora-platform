package com.soodora.test.factory;

import com.soodora.domain.post.Post;

import java.util.Date;

import lombok.NoArgsConstructor;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.TestConstants.ARTIST_USER;

@NoArgsConstructor(access = PROTECTED)
public class PostFactory {

    public static Post buildDefaultPost(String slug, String title) {
        return new Post(
                slug,
                title,
                "Content",
                "imageUrl",
                "my audio",
                "audioUrl",
                ARTIST_USER,
                emptyList(),
                emptyList(),
                emptyList(),
                new Date(),
                new Date());
    }

    public static Post buildDefaultPostWithoutAudio(String slug, String title) {
        return new Post(
                slug,
                title,
                "Content",
                "imageUrl",
                null,
                null,
                ARTIST_USER,
                emptyList(),
                emptyList(),
                emptyList(),
                new Date(),
                new Date());
    }

}
