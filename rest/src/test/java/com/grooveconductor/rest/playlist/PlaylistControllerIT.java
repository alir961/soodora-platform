package com.grooveconductor.rest.playlist;

import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.playlist.CreatePlaylistRequest;
import com.grooveconductor.application.playlist.UpdatePlaylistRequest;
import com.grooveconductor.application.playlist.PlaylistService;
import com.grooveconductor.application.playlist.PlaylistUnityException;
import com.grooveconductor.application.user.UnknownUserException;
import com.grooveconductor.domain.playlist.CreatePlaylistCommand;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.playlist.UpdatePlaylistCommand;
import com.grooveconductor.domain.user.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.grooveconductor.rest.JsonStringify.asJsonString;
import static com.grooveconductor.test.factory.PlaylistFactory.buildDefaultPlaylist;
import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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
    private AuthenticationService authenticationService;

    @MockBean
    private JwtTokenService jwtTokenService;

    private static final PlaylistDTO PLAYLIST_1 = buildDefaultPlaylist("playlistSlug1", "Playlist 1");
    private static final PlaylistDTO PLAYLIST_2 = buildDefaultPlaylist("playlistSlug2", "Playlist 2");
    private static final PlaylistDTO PLAYLIST_3 = buildDefaultPlaylist("playlistSlug3", "Playlist 3");

    @Test
    void createPlaylist_Success() throws Exception {
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description", "PUBLIC", "");
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isCreated());
    }

    @Test
    void createPlaylist_InvalidRequest() throws Exception {
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("", "Description", "", "");
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(new InvalidRequestException("Invalid request")).when(playlistService).createPlaylist(any(CreatePlaylistCommand.class), any(UserDTO.class));

        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createPlaylist_PlaylistNameUsed() throws Exception {
        UserDTO user = buildDefaultUser();
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description", "PUBLIC", "");

        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user@example.com");
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        doThrow(new PlaylistUnityException("Name is already used"))
            .when(playlistService).createPlaylist(any(CreatePlaylistCommand.class), eq(user));

        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.reason").value("Name is already used"));
    }

    @Test
    void updatePlaylist_Success() throws Exception {
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest(
            "Updated Playlist Name",
            "Updated Description",
            "PUBLIC",
            "");

        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        mockMvc.perform(put("/playlists/playlistSlug1")
                .contentType("application/json")
                .content(asJsonString(updatePlaylistRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isAccepted());
    }

    @Test
    void updatePlaylist_InvalidRequest() throws Exception {
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest(
            "",
            "",
            "PUBLIC",
            "");

        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(new InvalidRequestException("Invalid request"))
            .when(playlistService)
            .updatePlaylist(any(UpdatePlaylistCommand.class), eq("playlistSlug1"));

        mockMvc.perform(put("/playlists/playlistSlug1")
                .contentType("application/json")
                .content(asJsonString(updatePlaylistRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.reason").value("Invalid request"));
    }

    @Test
    void retrievePlaylists_Success() throws Exception {
        when(playlistService.retrieveAllPlaylists()).thenReturn(asList(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3));
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        mockMvc.perform(get("/playlists")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Empty() throws Exception {
        when(playlistService.retrieveAllPlaylists()).thenReturn(emptyList());
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        mockMvc.perform(get("/playlists")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Forbidden() throws Exception {
        mockMvc.perform(get("/playlists"))
            .andExpect(status().isForbidden());
    }

    @Test
    void retrievePlaylist_Success() throws Exception {
        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        mockMvc.perform(get("/playlists/playlistSlug1")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.slug").value("playlistSlug1"))
            .andExpect(jsonPath("$.name").value("Playlist 1"));
    }

    @Test
    void retrievePlaylist_Unknown() throws Exception {
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(UnknownUserException.class).when(playlistService).retrievePlaylistBySlug(anyString());

        mockMvc.perform(get("/playlists/unknown")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isNotFound());
    }

    @Test
    void retrievePlaylist_Forbidden() throws Exception {
        mockMvc.perform(get("/playlists/playlistSlug1"))
            .andExpect(status().isForbidden());
    }

    @Test
    void deletePlaylist_Success() throws Exception {
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doNothing().when(playlistService).deletePlaylistBySlug("playlistSlug1");

        mockMvc.perform(delete("/playlists/playlistSlug1")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
        verify(playlistService, times(1)).deletePlaylistBySlug("playlistSlug1");
    }

    @Test
    void deletePlaylist_Unknown() throws Exception {
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(UnknownUserException.class).when(playlistService).deletePlaylistBySlug(anyString());

        mockMvc.perform(delete("/playlists/unknown")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deletePlaylist_Forbidden() throws Exception {
        mockMvc.perform(delete("/playlists/playlistSlug1"))
            .andExpect(status().isForbidden());
    }

}
