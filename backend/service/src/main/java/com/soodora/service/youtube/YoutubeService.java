package com.soodora.service.youtube;

import com.soodora.domain.user.User;
import com.soodora.domain.youtube.YoutubeAdapter;
import com.soodora.domain.youtube.YoutubePlaylistDTO;
import com.soodora.domain.youtube.YoutubePlaylistItemDTO;
import com.soodora.domain.youtube.YoutubeServiceException;
import com.soodora.service.common.ExternalServiceException;
import com.soodora.service.oauth.OAuthTokenService;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.oauth.OAuthProvider.GOOGLE;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final OAuthTokenService oauthTokenService;
    private final YoutubeAdapter youtubeAdapter;

    public List<YoutubePlaylistDTO> getPlaylists(User user) throws ExternalServiceException {
        try {
            String accessToken = oauthTokenService.retrieveUserAccessToken(GOOGLE, user);

            return youtubeAdapter.fetchUserPlaylists(accessToken);
        } catch (YoutubeServiceException | IOException exception) {
            throw new ExternalServiceException(exception.getMessage());
        }
    }

    public List<YoutubePlaylistItemDTO> getPlaylistItems(User user, String playlistId)
            throws ExternalServiceException {
        try {
            String accessToken = oauthTokenService.retrieveUserAccessToken(GOOGLE, user);

            return youtubeAdapter.fetchPlaylistItems(accessToken, playlistId);
        } catch (YoutubeServiceException | IOException exception) {
            throw new ExternalServiceException(exception.getMessage());
        }
    }

}
