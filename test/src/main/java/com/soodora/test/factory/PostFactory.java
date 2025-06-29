package com.soodora.test.factory;

import com.soodora.domain.post.PostDTO;

import java.util.Date;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.factory.UserFactory.buildDefaultUser;

@NoArgsConstructor(access = PROTECTED)
public class PostFactory {

    public static PostDTO buildDefaultPost(String slug, String title) {
        return new PostDTO(
                slug,
                title,
                "Content",
                "imageUrl",
                "audioUrl",
                buildDefaultUser().toPublic(),
                0,
                0,
                0,
                new Date());
    }

    public static PostDTO buildDefaultPostWithoutAudio(String slug, String title) {
        return new PostDTO(
                slug,
                title,
                "Content",
                "imageUrl",
                null,
                buildDefaultUser().toPublic(),
                0,
                0,
                0,
                new Date());
    }

}
