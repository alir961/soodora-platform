package com.grooveconductor.domain.youtube;

import java.io.IOException;
import java.util.List;

public interface YoutubeAdapter {

    List<YoutubePlaylistDTO> fetchUserPlaylists(String accessToken) throws IOException;

}
