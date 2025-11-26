package com.soodora.api.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.playlist.CreatePlaylistCommand;
import com.soodora.domain.playlist.Playlist;
import com.soodora.domain.playlist.PlaylistFilter;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.domain.post.Post;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.playlist.*;
import com.soodora.service.post.UnknownPostException;
import com.soodora.service.user.UnknownUserException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.factory.PlaylistFactory.buildDefaultPrivatePlaylist;
import static com.soodora.domain.factory.PlaylistFactory.buildDefaultPublicPlaylist;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlaylistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaylistService playlistService;

    private static final Playlist PLAYLIST_1 = buildDefaultPublicPlaylist("playlistSlug1", "Playlist 1");
    private static final Playlist PLAYLIST_2 = buildDefaultPublicPlaylist("playlistSlug2", "Playlist 2");
    private static final Playlist PLAYLIST_3 = buildDefaultPublicPlaylist("playlistSlug3", "Playlist 3");
    private static final Playlist PLAYLIST_PRIVATE = buildDefaultPrivatePlaylist("playlistSlug", "Private Playlist");

    private static final MockMultipartFile FILE_PART = new MockMultipartFile("file", "test-image.jpg", "image/jpeg",
            "dummy image content".getBytes());

    @Test
    void givenValidCreatePlaylistRequest_whenCreatePlaylist_thenSuccess() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description",
                "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(createPlaylistRequest).getBytes());

        when(playlistService.createPlaylist(any(), any(), any())).thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists")
                .file(jsonPart)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("playlistSlug1"));
    }

    @Test
    void givenInvalidCreatePlaylistRequest_whenCreatePlaylist_thenBadRequest() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("", "Description", "");

        doThrow(new InvalidRequestException("Invalid request")).when(playlistService)
                .createPlaylist(any(CreatePlaylistCommand.class), eq(USER), any(FileData.class));

        // WHEN & THEN
        mockMvc.perform(post("/playlists")
                .contentType("application/json")
                .content(asJsonString(createPlaylistRequest))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenPlaylistNameAlreadyUsed_whenCreatePlaylist_thenBadRequest() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description",
                "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(createPlaylistRequest).getBytes());

        doThrow(new PlaylistUnityException("Name is already used")).when(playlistService)
                .createPlaylist(any(), eq(ARTIST_USER), any());

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists")
                .file(jsonPart)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Name is already used"));
    }

    @Test
    void givenUnauthenticatedUser_whenCreatePlaylist_thenForbidden() throws Exception {
        // GIVEN
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest("Playlist Name", "Description",
                "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(createPlaylistRequest).getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists")
                .file(jsonPart)
                .file(FILE_PART))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenPlaylistsExist_wheSearchPlaylists_thenSuccess() throws Exception {
        // GIVEN
        PlaylistFilter filter = new PlaylistFilter(empty(), empty(), empty());

        when(playlistService.retrievePlaylists(eq(filter), any(Pageable.class)))
                .thenReturn(new PageImpl<>(asList(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3)));

        // WHEN & THEN
        mockMvc.perform(get("/playlists/search"))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoPlaylistsExist_whenSearchPlaylists_thenSuccessWithEmptyList() throws Exception {
        // GIVEN
        PlaylistFilter filter = new PlaylistFilter(empty(), empty(), empty());

        when(playlistService.retrievePlaylists(eq(filter), any(Pageable.class)))
                .thenReturn(new PageImpl<>(emptyList()));

        // WHEN & THEN
        mockMvc.perform(get("/playlists/search"))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidPlaylistSlug_whenGetPlaylist_thenSuccess() throws Exception {
        // GIVEN
        Page<Post> posts = new PageImpl<>(PLAYLIST_1.getPosts());

        when(playlistService.retrievePlaylistBySlug("playlistSlug1"))
                .thenReturn(PLAYLIST_1);
        when(playlistService.retrievePlaylistPostsBySlug(eq("playlistSlug1"), any(Pageable.class)))
                .thenReturn(posts);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlist.slug").value("playlistSlug1"))
                .andExpect(jsonPath("$.posts.content").isArray());
    }

    @Test
    void givenOwnerUserAndPrivatePlaylist_whenGetPlaylist_thenSuccess() throws Exception {
        // GIVEN
        PageImpl<Post> page = new PageImpl<>(PLAYLIST_PRIVATE.getPosts());

        when(playlistService.retrievePlaylistBySlug("playlistSlug"))
                .thenReturn(PLAYLIST_PRIVATE);
        when(playlistService.retrievePlaylistPostsBySlug(eq("playlistSlug"), any(Pageable.class)))
                .thenReturn(page);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenUnknownPlaylistSlug_whenGetPlaylist_thenNotFound() throws Exception {
        // GIVEN
        doThrow(UnknownUserException.class).when(playlistService).retrievePlaylistBySlug("unknown");

        // WHEN & THEN
        mockMvc.perform(get("/playlists/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenPrivatePlaylistSlug_whenGetPlaylist_thenForbidden() throws Exception {
        // GIVEN
        PageImpl<Post> page = new PageImpl<>(PLAYLIST_PRIVATE.getPosts());

        when(playlistService.retrievePlaylistBySlug("playlistSlug"))
                .thenReturn(PLAYLIST_PRIVATE);
        when(playlistService.retrievePlaylistPostsBySlug(eq("playlistSlug"), any(Pageable.class)))
                .thenReturn(page);

        // WHEN & THEN
        mockMvc.perform(get("/playlists/playlistSlug"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidUpdatePlaylistRequest_whenUpdatePlaylist_thenSuccess() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest("Updated Playlist Name",
                "Updated Description", "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(updatePlaylistRequest).getBytes());

        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        when(playlistService.updatePlaylist(any(), any(), any())).thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER)))
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

        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        doThrow(new InvalidRequestException("Invalid request")).when(playlistService)
                .updatePlaylist(any(UpdatePlaylistCommand.class), eq("playlistSlug1"), any());

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Invalid request"));
    }

    @Test
    void givenAdminUser_whenUpdatePlaylist_thenSuccess() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest("", "", "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(updatePlaylistRequest).getBytes());

        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        when(playlistService.updatePlaylist(any(), any(), any())).thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ADMIN)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk());
    }

    @Test
    void givenAuthenticatedUser_whenUpdatePlaylist_thenForbidden() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest("", "", "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(updatePlaylistRequest).getBytes());

        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(FILE_PART)
                .with(user(new UserPrincipal(USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdatePlaylist_thenForbidden() throws Exception {
        // GIVEN
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest("", "", "PUBLIC");
        MockMultipartFile jsonPart = new MockMultipartFile("data", "", "application/json",
                asJsonString(updatePlaylistRequest).getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart("/playlists/playlistSlug1")
                .file(jsonPart)
                .file(FILE_PART)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPlaylistSlug_whenDeletePlaylist_thenSuccess() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1"))
                .thenReturn(PLAYLIST_1);
        doNothing().when(playlistService).deletePlaylistBySlug("playlistSlug1");

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
        verify(playlistService).deletePlaylistBySlug("playlistSlug1");
    }

    @Test
    void givenUnknownPlaylistSlug_whenDeletePlaylist_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPlaylistException("Playlist not found")).when(playlistService)
                .retrievePlaylistBySlug("unknown");

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/unknown")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticatedUser_whenDeletePlaylist_thenForbidden() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeletePlaylist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPlaylistAndPost_whenAddPostToPlaylist_thenSuccess() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        when(playlistService.addPostToPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(post("/playlists/playlistSlug1/posts/postSlug1")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlist.slug").value("playlistSlug1"));
    }

    @Test
    void givenUnknownPlaylist_whenAddPostToPlaylist_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPlaylistException("Playlist not found")).when(playlistService)
                .retrievePlaylistBySlug("unknown");

        // WHEN & THEN
        mockMvc.perform(post("/playlists/unknown/posts/postSlug1")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason").value("Playlist not found"));
    }

    @Test
    void givenUnknownPost_whenAddPostToPlaylist_thenNotFound() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        doThrow(new UnknownPostException("Post not found"))
                .when(playlistService).addPostToPlaylist(PLAYLIST_1, "unknown");

        // WHEN & THEN
        mockMvc.perform(post("/playlists/playlistSlug1/posts/unknown")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason").value("Post not found"));
    }

    @Test
    void givenAuthenticatedUser_whenAddPostToPlaylist_thenForbidden() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(post("/playlists/playlistSlug1/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenAddPostToPlaylist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/playlists/playlistSlug1/posts/postSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPlaylistAndPost_whenRemovePostFromPlaylist_thenSuccess() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        when(playlistService.removePostFromPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1/posts/postSlug1")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.content.length()").value(3));
    }

    @Test
    void givenUnknownPlaylist_whenRemovePostFromPlaylist_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPlaylistException("Playlist not found")).when(playlistService)
                .retrievePlaylistBySlug("playlistSlug1");

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1/posts/unknown")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason").value("Playlist not found"));
    }

    @Test
    void givenUnknownPost_whenRemovePostFromPlaylist_thenNotFound() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1")).thenReturn(PLAYLIST_1);
        doThrow(new UnknownPostException("Post not found"))
                .when(playlistService).removePostFromPlaylist(PLAYLIST_1, "unknown");

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1/posts/unknown")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason").value("Post not found"));
    }

    @Test
    void givenAuthenticatedUser_whenRemovePostFromPlaylist_thenForbidden() throws Exception {
        // GIVEN
        when(playlistService.retrievePlaylistBySlug("playlistSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenRemovePostFromPlaylist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/playlists/playlistSlug1/posts/postSlug1"))
                .andExpect(status().isForbidden());
    }

}
