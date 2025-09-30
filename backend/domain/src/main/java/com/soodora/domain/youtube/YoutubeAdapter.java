package com.soodora.domain.youtube;

import java.io.IOException;
import java.util.List;

public interface YoutubeAdapter {

    List<YoutubePlaylistDTO> fetchUserPlaylists(String accessToken) throws IOException;
    List<YoutubePlaylistItemDTO> fetchPlaylistItems(String accessToken, String playlistId) throws IOException;

}
