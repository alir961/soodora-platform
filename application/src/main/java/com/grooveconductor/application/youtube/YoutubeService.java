package com.grooveconductor.application.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.grooveconductor.application.oauth.OAuthTokenService;
import com.grooveconductor.domain.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final OAuthTokenService oauthTokenService;

    private final YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setApplicationName("GrooveConductor")
        .build();

    public PlaylistListResponse getPlaylists(UserDTO user) throws Exception {
        String accessToken = oauthTokenService.retrieveUserAccessToken(GOOGLE, user);
        YouTube.Playlists.List request = youtube.playlists()
            .list("snippet,contentDetails")
            .setMine(true)
            .setOauthToken(accessToken);

        return request.execute();
    }

}
