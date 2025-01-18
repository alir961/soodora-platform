package com.grooveconductor.domain.youtube;

import com.grooveconductor.domain.playlist.PlaylistPrivacy;

import java.util.Date;

public record YoutubePlaylistDTO(
    String id,
    String title,
    String description,
    Date publishedDate,
    String thumbnailUrl,
    PlaylistPrivacy privacy,
    YoutubeChannelDTO channel,
    long numberOfElements
) {

}
