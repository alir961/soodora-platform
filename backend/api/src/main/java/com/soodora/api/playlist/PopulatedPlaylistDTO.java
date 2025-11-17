package com.soodora.api.playlist;

import com.soodora.api.common.JsonResponse;
import com.soodora.api.post.PostDTO;
import com.soodora.domain.playlist.Playlist;
import com.soodora.domain.post.Post;

import org.springframework.data.domain.Page;

public record PopulatedPlaylistDTO(
        PlaylistDTO playlist,
        Page<PostDTO> posts) implements JsonResponse {

    public static PopulatedPlaylistDTO build(Playlist playlist, Page<Post> posts) {
        return new PopulatedPlaylistDTO(
                PlaylistDTO.build(playlist),
                posts.map(PostDTO::build));
    }

}
