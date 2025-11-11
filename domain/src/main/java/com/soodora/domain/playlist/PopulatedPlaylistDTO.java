package com.soodora.domain.playlist;

import com.soodora.domain.post.Post;

import org.springframework.data.domain.Page;

public record PopulatedPlaylistDTO(
        PlaylistDTO playlist,
        Page<Post> posts) {

}
