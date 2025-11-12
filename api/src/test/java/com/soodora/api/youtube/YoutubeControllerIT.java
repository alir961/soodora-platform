package com.soodora.api.youtube;

import com.soodora.domain.user.UserRepository;
import com.soodora.domain.youtube.YoutubePlaylistDTO;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.youtube.YoutubeService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.YoutubeFactory.buildDefaultYoutubePlaylist;

@SpringBootTest
@AutoConfigureMockMvc
class YoutubeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private YoutubeService youtubeService;

    @MockitoBean
    private UserRepository userRepository;

    private static final YoutubePlaylistDTO PLAYLIST_1 = buildDefaultYoutubePlaylist("1");
    private static final YoutubePlaylistDTO PLAYLIST_2 = buildDefaultYoutubePlaylist("2");
    private static final YoutubePlaylistDTO PLAYLIST_3 = buildDefaultYoutubePlaylist("3");
    private static final List<YoutubePlaylistDTO> PLAYLISTS = List.of(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3);

    @Test
    void getAuthenticatedUserYoutubePlaylists_Success() throws Exception {
        // GIVEN
        when(userRepository.find(createFromEmail(USER.getEmail().value()))).thenReturn(USER);
        when(youtubeService.getPlaylists(USER)).thenReturn(PLAYLISTS);

        // WHEN & THEN
        mockMvc.perform(get("/youtube/playlists")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[2].id").value("3"));
    }

    @Test
    void getAuthenticatedUserYoutubePlaylists_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/youtube/playlists"))
                .andExpect(status().isForbidden());
    }

}
