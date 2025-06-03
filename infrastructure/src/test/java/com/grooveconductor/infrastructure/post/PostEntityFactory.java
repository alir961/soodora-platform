package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.PostType;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.test.factory.PostFactory;

import java.util.Date;

import static java.util.Collections.emptyList;
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
                PostType.ANNOUNCE,
                user,
                emptyList(),
                emptyList(),
                0,
                0,
                new Date(),
                new Date());
    }

}
