package com.grooveconductor.application.youtube;

import com.grooveconductor.application.oauth.OAuthFlowException;
import com.grooveconductor.application.oauth.OAuthTokenService;
import com.grooveconductor.domain.youtube.YoutubeAdapter;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.test.TestConstants.ACCESS_TOKEN;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.YoutubeFactory.buildDefaultYoutubePlaylist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YoutubeServiceTest {

    @Mock
    private OAuthTokenService oauthTokenService;

    @Mock
    private YoutubeAdapter youtubeAdapter;

    private YoutubeService youtubeService;

    private static final YoutubePlaylistDTO PLAYLIST_1 = buildDefaultYoutubePlaylist("1");
    private static final YoutubePlaylistDTO PLAYLIST_2 = buildDefaultYoutubePlaylist("2");
    private static final YoutubePlaylistDTO PLAYLIST_3 = buildDefaultYoutubePlaylist("3");
    private static final List<YoutubePlaylistDTO> PLAYLISTS = List.of(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3);

    @BeforeEach
    void setUp() {
        youtubeService = new YoutubeService(oauthTokenService, youtubeAdapter);
    }

    @Test
    void getPlaylists_Success() throws Exception {
        // GIVEN

        when(oauthTokenService.retrieveUserAccessToken(GOOGLE, USER)).thenReturn(ACCESS_TOKEN);
        when(youtubeAdapter.fetchUserPlaylists(ACCESS_TOKEN)).thenReturn(PLAYLISTS);

        // WHEN
        List<YoutubePlaylistDTO> playlists = youtubeService.getPlaylists(USER);

        // THEN
        assertEquals(PLAYLISTS, playlists);
    }

    @Test
    void getPlaylists_InvalidOAuth() {
        // GIVEN
        when(oauthTokenService.retrieveUserAccessToken(GOOGLE, USER)).thenThrow(OAuthFlowException.class);

        // WHEN
        Executable executable = () -> youtubeService.getPlaylists(USER);

        // THEN
        assertThrows(OAuthFlowException.class, executable);
    }

}