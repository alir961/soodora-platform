package com.grooveconductor.infrastructure.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.domain.youtube.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.*;

@Component
public class YoutubeAdapterImpl implements YoutubeAdapter {

    private final YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
            .setApplicationName("GrooveConductor")
            .build();

    @Override
    public List<YoutubePlaylistDTO> fetchUserPlaylists(String accessToken) throws YoutubeServiceException, IOException {
        YouTube.Playlists.List request = youtube.playlists()
                .list("snippet,contentDetails,status")
                .setMine(true)
                .setOauthToken(accessToken);

        return request.execute().getItems().stream()
                .map(this::parsePlaylists)
                .toList();
    }

    @Override
    public List<YoutubePlaylistItemDTO> fetchPlaylistItems(String accessToken, String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems()
                .list("snippet,contentDetails,status")
                .setPlaylistId(playlistId)
                .setOauthToken(accessToken);

        return request.execute().getItems().stream()
                .map(this::parsePlaylistItem)
                .toList();
    }

    private YoutubePlaylistDTO parsePlaylists(Playlist playlist) throws YoutubeServiceException {
        PlaylistSnippet snippet = playlist.getSnippet();
        YoutubeChannelDTO channel = new YoutubeChannelDTO(snippet.getChannelId(), snippet.getChannelTitle());

        return new YoutubePlaylistDTO(
                playlist.getId(),
                snippet.getTitle(),
                snippet.getDescription(),
                new Date(snippet.getPublishedAt().getValue()),
                snippet.getThumbnails().getStandard().getUrl(),
                parsePrivacy(playlist.getStatus().getPrivacyStatus()),
                channel,
                playlist.getContentDetails().getItemCount());
    }

    private YoutubePlaylistItemDTO parsePlaylistItem(PlaylistItem item) {
        PlaylistItemSnippet snippet = item.getSnippet();

        return new YoutubePlaylistItemDTO(
                item.getId(),
                snippet.getTitle(),
                snippet.getDescription(),
                new Date(snippet.getPublishedAt().getValue()),
                snippet.getThumbnails().getStandard().getUrl(),
                parsePrivacy(item.getStatus().getPrivacyStatus()),
                parseChannel(snippet),
                snippet.getPosition());
    }

    private YoutubeChannelDTO parseChannel(PlaylistItemSnippet snippet) {
        String channelId = snippet.get("videoOwnerChannelId").toString();
        String channelTitle = snippet.get("videoOwnerChannelTitle").toString()
                .replace(" - Topic", "");

        return new YoutubeChannelDTO(channelId, channelTitle);
    }

    private PlaylistPrivacy parsePrivacy(String privacy) throws YoutubeServiceException {
        return switch (privacy) {
            case "private" -> PRIVATE;
            case "public" -> PUBLIC;
            case "unlisted" -> UNLISTED;
            default -> throw new YoutubeServiceException("Unknown playlist privacy " + privacy);
        };
    }

}
