package com.grooveconductor.test.factory;

import com.grooveconductor.domain.post.PostDTO;
import lombok.NoArgsConstructor;

import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class PostFactory {

  public static PostDTO buildDefaultPost(String slug, String title) {
    return new PostDTO(
        slug,
        title,
        "Content",
        buildDefaultUser());
  }

}