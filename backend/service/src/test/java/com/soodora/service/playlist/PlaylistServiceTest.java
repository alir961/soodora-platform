package com.soodora.service.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.service.post.UnknownPostException;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.PlaylistFactory.buildDefaultPublicPlaylist;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    private PlaylistService playlistService;

    private static final String SLUG_1 = "slug1";
    private static final Playlist PLAYLIST_1 = buildDefaultPublicPlaylist(SLUG_1, "Name 1");
    private static final Playlist PLAYLIST_2 = buildDefaultPublicPlaylist("slug2", "Name 2");
    private static final Playlist PLAYLIST_3 = buildDefaultPublicPlaylist("slug3", "Name 3");
    private static final List<Playlist> PLAYLISTS = List.of(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3);
    private static final FileData FILE_DATA = new FileData(
            "dummy image content".getBytes(),
            "test-image.jpg",
            "image/jpeg");

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService(playlistRepository);
    }

    @Test
    void givenValidCreatePlaylistCommand_whenCreatePlaylist_thenPlaylistIsSaved() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("New Playlist", "Description", PUBLIC);

        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.getSlug())).thenReturn(false);
        when(playlistRepository.save(any(), eq(USER), eq(FILE_DATA))).thenReturn(PLAYLIST_1);

        // WHEN
        Playlist createdPlaylist = playlistService.createPlaylist(command, USER, FILE_DATA);

        // THEN
        assertNotNull(createdPlaylist);
        verify(playlistRepository).save(any(), eq(USER), eq(FILE_DATA));
    }

    @Test
    void givenPlaylistNameAlreadyUsed_whenCreatePlaylist_thenThrowsPlaylistUnityException() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("Existing Playlist", "Description", PUBLIC);

        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.getSlug())).thenReturn(true);

        // WHEN
        Executable executable = () -> playlistService.createPlaylist(command, USER, FILE_DATA);

        // THEN
        assertThrows(PlaylistUnityException.class, executable);
    }

    @Test
    void givenValidUpdateCommand_whenUpdatePlaylist_thenPlaylistIsUpdated() {
        // GIVEN
        UpdatePlaylistCommand updateCommand = new UpdatePlaylistCommand("Updated Name", "Updated Description", PUBLIC);

        when(playlistRepository.update(updateCommand, SLUG_1, FILE_DATA)).thenReturn(PLAYLIST_1);

        // WHEN
        Playlist updatedPlaylist = playlistService.updatePlaylist(updateCommand, SLUG_1, FILE_DATA);

        // THEN
        assertEquals(PLAYLIST_1, updatedPlaylist);
        verify(playlistRepository).update(any(UpdatePlaylistCommand.class), eq(SLUG_1), eq(FILE_DATA));
    }

    @Test
    void givenUnknownSlug_whenUpdatePlaylist_thenThrowsPlaylistUnityException() {
        // GIVEN
        UpdatePlaylistCommand updateCommand = new UpdatePlaylistCommand("Updated Name", "Updated Description", PUBLIC);

        when(playlistRepository.update(updateCommand, SLUG_1, FILE_DATA))
                .thenThrow(new UnknownSlugException("Slug not found"));

        // WHEN / THEN
        assertThrows(PlaylistUnityException.class,
                () -> playlistService.updatePlaylist(updateCommand, SLUG_1, FILE_DATA));
    }

    @Test
    void givenPlaylistsExist_whenRetrieveAllPlaylists_thenReturnAllPlaylists() {
        // GIVEN
        when(playlistRepository.findAll()).thenReturn(PLAYLISTS);

        // WHEN
        List<Playlist> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists);
    }

    @Test
    void givenNoPlaylistsExist_whenRetrieveAllPlaylists_thenReturnEmptyList() {
        // GIVEN
        when(playlistRepository.findAll()).thenReturn(emptyList());

        // WHEN
        List<Playlist> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertTrue(retrievedPlaylists.isEmpty());
    }

    @Test
    void givenNoFilter_whenRetrievePlaylists_thenReturnAllPlaylistsPage() {
        // GIVEN
        PlaylistFilter noFilter = new PlaylistFilter(empty(), empty(), empty());
        Page<Playlist> page = new PageImpl<>(PLAYLISTS);
        when(playlistRepository.findByFilter(eq(noFilter), any(Pageable.class))).thenReturn(page);

        // WHEN
        Page<Playlist> retrievedPlaylists = playlistService.retrievePlaylists(noFilter, Pageable.unpaged());

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists.getContent());
    }

    @Test
    void givenExistingPlaylistSlug_whenRetrievePlaylistBySlug_thenReturnFullPlaylist() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1)).thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.retrievePlaylistBySlug(SLUG_1);

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenUnknownPlaylistSlug_whenRetrievePlaylistBySlug_thenThrowUnknownSlugException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1)).thenThrow(new UnknownSlugException("Not found"));

        // WHEN
        Executable executable = () -> playlistService.retrievePlaylistBySlug(SLUG_1);

        // THEN
        assertThrows(UnknownPlaylistException.class, executable);
    }

    @Test
    void givenExistingPlaylistSlug_whenDeletePlaylist_thenDeletesPlaylist() {
        // GIVEN
        doNothing().when(playlistRepository).deletePlaylistAndUnlinkPosts(SLUG_1);

        // WHEN
        assertDoesNotThrow(() -> playlistService.deletePlaylistBySlug(SLUG_1));

        // THEN
        verify(playlistRepository).deletePlaylistAndUnlinkPosts(SLUG_1);
    }

    @Test
    void givenUnknownPlaylistSlug_whenDeletePlaylist_thenThrowsUnknownPlaylistException() {
        // GIVEN
        doThrow(new UnknownSlugException("Not found"))
                .when(playlistRepository).deletePlaylistAndUnlinkPosts(SLUG_1);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylistBySlug(SLUG_1);

        // THEN
        assertThrows(UnknownPlaylistException.class, executable);
    }

    @Test
    void givenValidPost_whenAddPostToPlaylist_thenPostIsAdded() {
        // GIVEN
        when(playlistRepository.addPostToPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.addPostToPlaylist(PLAYLIST_1, "postSlug1");

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenUnknownPlaylistSlug_whenAddPostToPlaylist_thenThrowsPlaylistUnityException() {
        // GIVEN
        when(playlistRepository.addPostToPlaylist(PLAYLIST_1, "postSlug1"))
                .thenThrow(new UnknownSlugException("not found"));

        // WHEN
        Executable executable = () -> playlistService.addPostToPlaylist(PLAYLIST_1, "postSlug1");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidPost_whenRemovePostFromPlaylist_thenPostIsRemoved() {
        // GIVEN
        when(playlistRepository.removePostFromPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.removePostFromPlaylist(PLAYLIST_1, "postSlug1");

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenUnknownPlaylistSlug_whenRemovePostFromPlaylist_thenThrowsPlaylistUnityException() {
        // GIVEN
        when(playlistRepository.removePostFromPlaylist(PLAYLIST_1, "postSlug1"))
                .thenThrow(new UnknownSlugException("Not found"));

        // WHEN
        Executable executable = () -> playlistService.removePostFromPlaylist(PLAYLIST_1, "postSlug1");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

}
