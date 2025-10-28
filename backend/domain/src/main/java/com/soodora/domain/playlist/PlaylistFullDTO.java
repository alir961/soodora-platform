package com.soodora.domain.playlist;

import com.soodora.domain.post.PostDTO;

import org.springframework.data.domain.Page;

public record PlaylistFullDTO(
        PlaylistDTO playlistSimpleDTO,
        Page<PostDTO> postDTO) {

}
