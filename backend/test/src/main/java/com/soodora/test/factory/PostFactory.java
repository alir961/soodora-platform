package com.soodora.test.factory;

import com.soodora.domain.post.PostDTO;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.test.factory.ArtistFactory.buildArtistDTO;

@NoArgsConstructor(access = PROTECTED)
public class PostFactory {

    public static PostDTO buildDefaultPost(String slug, String title) {
        return new PostDTO(
                slug,
                title,
                "Content",
                "imageUrl",
                "my audio",
                "audioUrl",
                buildArtistDTO("id"),
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
                null,
                buildArtistDTO("id"),
                0,
                0,
                0,
                new Date());
    }

    public static Page<PostDTO> buildPostsPage(List<PostDTO> posts) {
        return new PageImpl<>(posts);
    }

}
