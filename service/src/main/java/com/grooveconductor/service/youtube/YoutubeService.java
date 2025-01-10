package com.grooveconductor.service.youtube;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.youtube.YoutubeAdapter;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;
import com.grooveconductor.service.common.ExternalServiceException;
import com.grooveconductor.service.oauth.OAuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
        } catch (IOException exception) {
            throw new ExternalServiceException(exception.getMessage());
        }
    }

}
