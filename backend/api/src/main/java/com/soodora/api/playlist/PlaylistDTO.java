package com.soodora.api.playlist;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.common.value.Url;
import com.soodora.domain.playlist.Playlist;
import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public record PlaylistDTO(
        String slug,
        String name,
        String description,
        int numberOfTracks,
        PlaylistPrivacy privacy,
        String imageUrl,
        PublicUserDTO owner,
        Date createdAt,
        Date updatedAt) implements JsonResponse {

    public static PlaylistDTO build(Playlist playlist) {
        Url imageUrl = playlist.getImageUrl();

        return new PlaylistDTO(
                playlist.getSlug(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getPosts().size(),
                playlist.getPrivacy(),
                imageUrl == null ? null : imageUrl.value(),
                PublicUserDTO.build(playlist.getOwner()),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt());
    }

}
