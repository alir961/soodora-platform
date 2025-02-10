package com.grooveconductor.test.factory;

import com.grooveconductor.domain.youtube.YoutubeChannelDTO;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;

import java.util.Date;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.PUBLIC;

@NoArgsConstructor(access = PROTECTED)
public class YoutubeFactory {

    public static final Date PUBLISH_DATE = new Date();

    public static YoutubePlaylistDTO buildDefaultYoutubePlaylist() {
        return new YoutubePlaylistDTO(
                "id",
                "Title",
                "Description",
                PUBLISH_DATE,
                "http://youtube.com/thumbnail.jpg",
                PUBLIC,
                buildDefaultYoutubeChannel(),
                10);
    }

    public static YoutubePlaylistDTO buildDefaultYoutubePlaylist(String id) {
        return new YoutubePlaylistDTO(
                id,
                "Title #" + id,
                "Description",
                PUBLISH_DATE,
                "http://youtube.com/thumbnail.jpg",
                PUBLIC,
                buildDefaultYoutubeChannel(),
                10);
    }

    public static YoutubeChannelDTO buildDefaultYoutubeChannel() {
        return new YoutubeChannelDTO(
                "id",
                "Title");
    }

}
