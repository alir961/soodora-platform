package com.soodora.infrastructure.post;

import com.soodora.infrastructure.user.UserEntity;
import com.soodora.test.factory.PostFactory;

import java.util.Date;

import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;

public class PostEntityFactory extends PostFactory {

    public static PostEntity buildDefaultPostEntity(UserEntity user) {
        return new PostEntity(
                randomUUID(),
                "slug",
                "Post 1",
                "Content",
                "imageUrl",
                "audioUrl",
                user,
                emptySet(),
                emptySet(),
                emptySet(),
                new Date(),
                new Date());
    }

}
