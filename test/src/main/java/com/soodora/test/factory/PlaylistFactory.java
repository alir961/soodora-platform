package com.soodora.test.factory;

import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.PopulatedPlaylistDTO;
import com.soodora.domain.post.Post;
import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.PostFactory.buildDefaultPost;

@NoArgsConstructor(access = PROTECTED)
public class PlaylistFactory {

    public static PlaylistDTO buildDefaultPlaylist(String slug, String name) {
        return new PlaylistDTO(
                slug,
                name,
                "Description",
                0,
                PUBLIC,
                "imageUrl",
                PublicUserDTO.build(USER),
                new Date(),
                new Date());
    }

    public static PopulatedPlaylistDTO buildDefaultPlaylistFull(String slug, String name) {
        PlaylistDTO playlistDTO = new PlaylistDTO(
                slug,
                name,
                "Description",
                0,
                PUBLIC,
                "imageUrl",
                PublicUserDTO.build(USER),
                new Date(),
                new Date());
        List<Post> posts = List.of(
                buildDefaultPost("postSlug1", "Post 1"),
                buildDefaultPost("postSlug2", "Post 2"),
                buildDefaultPost("postSlug3", "Post 3"));
        Page<Post> postPage = new PageImpl<>(posts);

        return new PopulatedPlaylistDTO(playlistDTO, postPage);
    }

}
