package com.grooveconductor.api.playlist;

import com.grooveconductor.domain.playlist.CreatePlaylistCommand;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.playlist.UpdatePlaylistCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.playlist.CreatePlaylistRequest;
import com.grooveconductor.service.playlist.PlaylistService;
import com.grooveconductor.service.playlist.PlaylistUnityException;
import com.grooveconductor.service.playlist.UpdatePlaylistRequest;
import com.grooveconductor.service.user.UnknownUserException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.grooveconductor.api.JsonStringify.asJsonString;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.PlaylistFactory.buildDefaultPlaylist;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaylistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaylistService playlistService;

    @MockBean
    private UserRepository userRepository;

    private static final PlaylistDTO PLAYLIST_1 = buildDefaultPlaylist("playlistSlug1", "Playlist 1");
    private static final PlaylistDTO PLAYLIST_2 = buildDefaultPlaylist("playlistSlug2", "Playlist 2");
    private static final PlaylistDTO PLAYLIST_3 = buildDefaultPlaylist("playlistSlug3", "Playlist 3");

    @Test
    void createPlaylist_Success() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest(
                "Playlist Name",
                "Description",
                0,
                "PUBLIC",
                ""
        );

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                        .contentType("application/json")
                        .content(asJsonString(createPlaylistRequest))
                        .with(user(USER.email())))
                .andExpect(status().isCreated());
    }

    @Test
    void createPlaylist_InvalidRequest() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest(
                "",
                "Description",
                0,
                "",
                ""
        );

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request")).when(playlistService).createPlaylist(any(CreatePlaylistCommand.class), any(UserDTO.class));

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                        .contentType("application/json")
                        .content(asJsonString(createPlaylistRequest))
                        .with(user(USER.email())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlaylist_PlaylistNameUsed() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest(
                "Playlist Name",
                "Description",
                0,
                "PUBLIC",
                ""
        );

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new PlaylistUnityException("Name is already used"))
                .when(playlistService).createPlaylist(any(), any());

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                        .contentType("application/json")
                        .content(asJsonString(createPlaylistRequest))
                        .with(user(USER.email())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Name is already used"));
    }

    @Test
    void updatePlaylist_Success() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest(
                "Updated Playlist Name",
                "Updated Description",
                0,
                "PUBLIC",
                ""
        );

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(put("/playlists/playlistSlug1")
                        .contentType("application/json")
                        .content(asJsonString(updatePlaylistRequest))
                        .with(user(USER.email())))
                .andExpect(status().isAccepted());
    }

    @Test
    void updatePlaylist_InvalidRequest() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest(
                "",
                "",
                0,
                "PUBLIC",
                ""
        );

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request"))
                .when(playlistService)
                .updatePlaylist(any(UpdatePlaylistCommand.class), eq("playlistSlug1"));

        // WHEN & THEN
        mockMvc.perform(put("/playlists/playlistSlug1")
                        .contentType("application/json")
                        .content(asJsonString(updatePlaylistRequest))
                        .with(user(USER.email())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Invalid request"));
    }

    @Test
    void retrievePlaylists_Success() throws Exception {
        // GIVEN
        when(playlistService.retrieveAllPlaylists()).thenReturn(asList(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3));
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/playlists")
                        .with(user(USER.email())))
                .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Empty() throws Exception {
        // GIVEN
        when(playlistService.retrieveAllPlaylists()).thenReturn(emptyList());
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/playlists")
                        .with(user(USER.email())))
                .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/playlists"))
                .andExpect(status().isForbidden());
    }

    @Test
    void retrievePlaylist_Success() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug1")
                        .with(user(USER.email())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("playlistSlug1"))
                .andExpect(jsonPath("$.name").value("Playlist 1"));
    }

    @Test
    void retrievePlaylist_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(playlistService).retrievePlaylistBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(get("/playlists/unknown")
                        .with(user(USER.email())))
                .andExpect(status().isNotFound());
    }

    @Test
    void retrievePlaylist_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePlaylist_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(playlistService).deletePlaylistBySlug("playlistSlug1");

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1")
                        .with(user(USER.email())))
                .andExpect(status().isOk());
        verify(playlistService, times(1)).deletePlaylistBySlug("playlistSlug1");
    }

    @Test
    void deletePlaylist_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(playlistService).deletePlaylistBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/unknown")
                        .with(user(USER.email())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlaylist_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1"))
                .andExpect(status().isForbidden());
    }

}
