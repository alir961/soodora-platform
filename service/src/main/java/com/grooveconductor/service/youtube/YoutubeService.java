package com.grooveconductor.service.youtube;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.youtube.YoutubeAdapter;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;
import com.grooveconductor.domain.youtube.YoutubePlaylistItemDTO;
import com.grooveconductor.domain.youtube.YoutubeServiceException;
import com.grooveconductor.service.common.ExternalServiceException;
import com.grooveconductor.service.oauth.OAuthTokenService;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final OAuthTokenService oauthTokenService;
    private final YoutubeAdapter youtubeAdapter;

    public List<YoutubePlaylistDTO> getPlaylists(UserDTO user) throws ExternalServiceException {
        try {
            String accessToken = oauthTokenService.retrieveUserAccessToken(GOOGLE, user);

            return youtubeAdapter.fetchUserPlaylists(accessToken);
        } catch (YoutubeServiceException | IOException exception) {
            throw new ExternalServiceException(exception.getMessage());
        }
    }

    public List<YoutubePlaylistItemDTO> getPlaylistItems(UserDTO user, String playlistId)
            throws ExternalServiceException {
        try {
            String accessToken = oauthTokenService.retrieveUserAccessToken(GOOGLE, user);

            return youtubeAdapter.fetchPlaylistItems(accessToken, playlistId);
        } catch (YoutubeServiceException | IOException exception) {
            throw new ExternalServiceException(exception.getMessage());
        }
    }

}
