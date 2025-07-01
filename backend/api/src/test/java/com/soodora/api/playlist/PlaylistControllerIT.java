package com.soodora.api.playlist;

import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.playlist.CreatePlaylistRequest;
import com.soodora.service.playlist.PlaylistService;
import com.soodora.service.playlist.PlaylistUnityException;
import com.soodora.service.playlist.UpdatePlaylistRequest;
import com.soodora.service.user.UnknownUserException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.PlaylistFactory.buildDefaultPlaylist;

@SpringBootTest
@AutoConfigureMockMvc
class PlaylistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaylistService playlistService;

    @MockitoBean
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
                "");

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .with(user(new UserPrincipal(USER))))
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
                "");

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request")).when(playlistService)
                .createPlaylist(any(CreatePlaylistCommand.class), any(UserDTO.class));

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .with(user(new UserPrincipal(USER))))
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
                "");

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new PlaylistUnityException("Name is already used"))
                .when(playlistService).createPlaylist(any(), eq(USER));

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .with(user(new UserPrincipal(USER))))
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
                "");

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(put("/playlists/playlistSlug1")
                .contentType("application/json")
                .content(asJsonString(updatePlaylistRequest))
                .with(user(new UserPrincipal(USER))))
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
                "");

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request"))
                .when(playlistService)
                .updatePlaylist(any(UpdatePlaylistCommand.class), eq("playlistSlug1"));

        // WHEN & THEN
        mockMvc.perform(put("/playlists/playlistSlug1")
                .contentType("application/json")
                .content(asJsonString(updatePlaylistRequest))
                .with(user(new UserPrincipal(USER))))
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
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Empty() throws Exception {
        // GIVEN
        when(playlistService.retrieveAllPlaylists()).thenReturn(emptyList());
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/playlists")
                .with(user(new UserPrincipal(USER))))
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
                .with(user(new UserPrincipal(USER))))
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
                .with(user(new UserPrincipal(USER))))
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
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(playlistService).deletePlaylistBySlug("playlistSlug1");
    }

    @Test
    void deletePlaylist_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(playlistService).deletePlaylistBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlaylist_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1"))
                .andExpect(status().isForbidden());
    }

}
