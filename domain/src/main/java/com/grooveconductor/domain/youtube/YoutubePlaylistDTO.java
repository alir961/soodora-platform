package com.grooveconductor.domain.youtube;

import java.util.Date;

public record YoutubePlaylistDTO(
    String id,
    String title,
    String description,
    Date publishedDate,
    String thumbnailUrl,
    YoutubeChannelDTO channel,
    long numberOfElements
) {

}
