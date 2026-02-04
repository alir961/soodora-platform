package com.soodora.domain.factory;

import com.soodora.domain.post.Post;

import java.util.Date;

import lombok.NoArgsConstructor;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.factory.UserFactory.buildDefaultArtistUser;

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
                buildDefaultArtistUser(),
                emptyList(),
                emptyList(),
                emptyList(),
                new Date(),
                new Date());
    }

}
