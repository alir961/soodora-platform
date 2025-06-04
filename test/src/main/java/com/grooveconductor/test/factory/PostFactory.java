package com.grooveconductor.test.factory;

import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostType;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;

@NoArgsConstructor(access = PROTECTED)
public class PostFactory {

    public static PostDTO buildDefaultPost(String slug, String title) {
        return new PostDTO(
                slug,
                title,
                "Content",
                "imageUrl",
                "audioUrl",
                PostType.ANNOUNCE,
                buildDefaultUser(),
                0,
                0);
    }

}
