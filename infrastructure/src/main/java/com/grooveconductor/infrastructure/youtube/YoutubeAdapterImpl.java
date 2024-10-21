package com.grooveconductor.infrastructure.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.grooveconductor.domain.youtube.YoutubeAdapter;
import com.grooveconductor.domain.youtube.YoutubeChannelDTO;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class YoutubeAdapterImpl implements YoutubeAdapter {

    private final YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setApplicationName("GrooveConductor")
        .build();

    @Override
    public List<YoutubePlaylistDTO> fetchUserPlaylists(String accessToken) throws IOException {
        YouTube.Playlists.List request = youtube.playlists()
            .list("snippet,contentDetails")
            .setMine(true)
            .setOauthToken(accessToken);

        return request.execute().getItems().stream()
            .map(this::parsePlaylists)
            .toList();
    }

    private YoutubePlaylistDTO parsePlaylists(Playlist playlist) {
        PlaylistSnippet snippet = playlist.getSnippet();
        YoutubeChannelDTO channel = new YoutubeChannelDTO(snippet.getChannelId(), snippet.getChannelTitle());

        return new YoutubePlaylistDTO(
            playlist.getId(),
            snippet.getTitle(),
            snippet.getDescription(),
            new Date(snippet.getPublishedAt().getValue()),
            snippet.getThumbnails().getStandard().getUrl(),
            channel,
            playlist.getContentDetails().getItemCount()
        );
    }

}
