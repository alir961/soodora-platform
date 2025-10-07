package com.soodora.api.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.PlaylistFullDTO;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.playlist.*;
import com.soodora.service.user.UnknownUserException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
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
import static com.soodora.test.factory.PlaylistFactory.*;
import static com.soodora.test.factory.PostFactory.buildDefaultPost;
import static com.soodora.test.factory.PostFactory.buildPostsPage;

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
    private static final PlaylistFullDTO PLAYLIST_4 = buildDefaultPlaylistFull("playlistSlug4", "Playlist 4");

    @Test
    void givenValidCreatePlaylistRequest_whenCreatePlaylist_thenSuccess() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description",
                "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(createPlaylistRequest).getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "test-image.jpg", "image/jpeg",
                "dummy image content".getBytes());
        PlaylistDTO mockedPlaylistDTO = buildDefaultPlaylist("playlistSlug1", "Playlist 1");

        when(playlistService.createPlaylist(any(), any(), any())).thenReturn(mockedPlaylistDTO);

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("playlistSlug1"));
    }

    @Test
    void givenInvalidCreatePlaylistRequest_whenCreatePlaylist_thenBadRequest() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("", "Description", "");

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request")).when(playlistService)
                .createPlaylist(any(CreatePlaylistCommand.class), eq(USER), any(FileData.class));

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenPlaylistNameAlreadyUsed_whenCreatePlaylist_thenBadRequest() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description",
                "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(createPlaylistRequest).getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "test-image.jpg", "image/jpeg",
                "dummy image content".getBytes());

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doThrow(new PlaylistUnityException("Name is already used")).when(playlistService)
                .createPlaylist(any(), eq(USER), any());

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Name is already used"));
    }

    @Test
    void givenValidUpdatePlaylistRequest_whenUpdatePlaylist_thenSuccess() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest("Updated Playlist Name",
                "Updated Description", "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(updatePlaylistRequest).getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "test-image.jpg", "image/jpeg",
                "dummy image content".getBytes());

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidUpdatePlaylistRequest_whenUpdatePlaylist_thenBadRequest() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest("", "", "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(updatePlaylistRequest).getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "test-image.jpg", "image/jpeg",
                "dummy image content".getBytes());

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request")).when(playlistService)
                .updatePlaylist(any(UpdatePlaylistCommand.class), eq("playlistSlug1"), any());

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Invalid request"));
    }

    @Test
    void givenPlaylistsExist_whenRetrievePlaylists_thenSuccess() throws Exception {
        // GIVEN
        when(playlistService.retrieveAllPlaylists()).thenReturn(asList(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3));
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/search")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoPlaylistsExist_whenRetrievePlaylists_thenSuccessWithEmptyList() throws Exception {
        // GIVEN
        when(playlistService.retrieveAllPlaylists()).thenReturn(emptyList());
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/search")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoUser_whenRetrievePlaylists_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/playlists"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPlaylistSlug_whenRetrievePlaylistWithPosts_thenSuccess() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug(eq("playlistSlug4"), any(Pageable.class))).thenReturn(PLAYLIST_4);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug4")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistSimpleDTO.slug").value("playlistSlug4"))
                .andExpect(jsonPath("$.postDTO.content").isArray());
    }

    @Test
    void givenUnknownPlaylistSlug_whenRetrievePlaylistWithPosts_thenNotFound() throws Exception {
        // GIVEN
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(playlistService).retrievePlaylistBySlug(anyString(),
                any(Pageable.class));

        // WHEN & THEN
        mockMvc.perform(get("/playlists/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNoUser_whenRetrievePlaylist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPlaylistAndPost_whenAddPostToPlaylist_thenSuccess() throws Exception {
        // GIVEN
        PlaylistFullDTO playlistBefore = buildDefaultPlaylistFull("playlistSlug4", "Playlist 4");
        Page<PostDTO> postsAfter = buildPostsPage(
                asList(buildDefaultPost("postSlug1", "Post 1"), buildDefaultPost("postSlug2", "Post 2")));
        PlaylistFullDTO playlistAfterAdd = new PlaylistFullDTO(playlistBefore.playlistSimpleDTO(), postsAfter);

        when(playlistService.addPostToPlaylist(eq("playlistSlug4"), eq("postSlug1"), any(Pageable.class)))
                .thenReturn(playlistAfterAdd);

        // WHEN & THEN
        mockMvc.perform(post("/playlists/playlistSlug4/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistFullDTO.playlistSimpleDTO.slug").value("playlistSlug4"))
                .andExpect(jsonPath("$.playlistFullDTO.postDTO.content.length()").value(2));
    }

    @Test
    void givenValidPlaylistAndPost_whenRemovePostFromPlaylist_thenSuccess() throws Exception {
        // GIVEN
        PlaylistFullDTO playlistBefore = buildDefaultPlaylistFull("playlistSlug4", "Playlist 4");
        Page<PostDTO> postsAfter = buildPostsPage(List.of(buildDefaultPost("postSlug2", "Post 2")));
        PlaylistFullDTO playlistAfterRemove = new PlaylistFullDTO(playlistBefore.playlistSimpleDTO(), postsAfter);

        when(playlistService.removePostFromPlaylist(eq("playlistSlug4"), eq("postSlug1"), any(Pageable.class)))
                .thenReturn(playlistAfterRemove);

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug4/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistFullDTO.postDTO.content.length()").value(1));
    }

    @Test
    void givenUnknownPlaylist_whenAddPost_thenBadRequest() throws Exception {
        // GIVEN
        doThrow(new PlaylistUnityException("Playlist not found")).when(playlistService).addPostToPlaylist(anyString(),
                anyString(), any(Pageable.class));

        // WHEN & THEN
        mockMvc.perform(post("/playlists/unknown/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Playlist not found"));
    }

    @Test
    void givenUnknownPost_whenRemovePost_thenBadRequest() throws Exception {
        // GIVEN
        doThrow(new PlaylistUnityException("Post not found")).when(playlistService).removePostFromPlaylist(anyString(),
                anyString(), any(Pageable.class));

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1/posts/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Post not found"));
    }

    @Test
    void givenValidPlaylistSlug_whenDeletePlaylist_thenSuccess() throws Exception {
        // GIVEN
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doNothing().when(playlistService).deletePlaylistBySlug("playlistSlug1");

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(playlistService).deletePlaylistBySlug("playlistSlug1");
    }

    @Test
    void givenUnknownPlaylistSlug_whenDeletePlaylist_thenNotFound() throws Exception {
        // GIVEN
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(playlistService).deletePlaylistBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNoUser_whenDeletePlaylist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1"))
                .andExpect(status().isForbidden());
    }

}
