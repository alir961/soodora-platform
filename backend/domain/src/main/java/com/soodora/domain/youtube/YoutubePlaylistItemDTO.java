package com.soodora.domain.youtube;

import com.soodora.domain.playlist.PlaylistPrivacy;

import java.util.Date;

public record YoutubePlaylistItemDTO(
        String id,
        String title,
        String description,
        Date publishedDate,
        String thumbnailUrl,
        PlaylistPrivacy privacy,
        YoutubeChannelDTO channel,
        long position) {
}
