package com.soodora.test.factory;

import com.soodora.domain.post.Post;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.NoArgsConstructor;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.factory.UserFactory.buildDefaultArtistUser;

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

    public static Post buildDefaultPostWithoutAudio(String slug, String title) {
        return new Post(
                slug,
                title,
                "Content",
                "imageUrl",
                null,
                null,
                buildDefaultArtistUser(),
                emptyList(),
                emptyList(),
                emptyList(),
                new Date(),
                new Date());
    }

    public static Page<Post> buildPostsPage(List<Post> posts) {
        return new PageImpl<>(posts);
    }

}
