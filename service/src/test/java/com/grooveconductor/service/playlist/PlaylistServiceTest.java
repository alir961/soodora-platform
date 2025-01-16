package com.grooveconductor.service.playlist;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.playlist.*;
import com.grooveconductor.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.grooveconductor.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.PlaylistFactory.buildDefaultPlaylist;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    private PlaylistService playlistService;

    private static final PlaylistDTO PLAYLIST_1 = buildDefaultPlaylist("slug1", "Name 1");
    private static final PlaylistDTO PLAYLIST_2 = buildDefaultPlaylist("slug2", "Name 2");
    private static final PlaylistDTO PLAYLIST_3 = buildDefaultPlaylist("slug3", "Name 3");
    private static final String SLUG_1 = "slug1";
    private static final List<PlaylistDTO> PLAYLISTS = List.of(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3);

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService(userRepository, playlistRepository);
    }

    @Test
    void createPlaylist_Success() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("New Playlist", "Description", 0, PUBLIC, "imageUrl");

        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.slug())).thenReturn(false);
        when(playlistRepository.save(any(), eq(USER))).thenReturn(PLAYLIST_1);

        // WHEN
        playlistService.createPlaylist(command, USER);

        // THEN
        verify(playlistRepository).save(any(), eq(USER));
    }

    @Test
    void createPlaylist_NameAlreadyUsed() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("Existing Playlist", "Description", 0, PUBLIC, "imageUrl");

        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.slug())).thenReturn(true);

        // WHEN
        Executable executable = () -> playlistService.createPlaylist(command, USER);

        // THEN
        assertThrows(PlaylistUnityException.class, executable);
    }

    @Test
    void updatePlaylist_Success() {
        UpdatePlaylistCommand updateCommand = new UpdatePlaylistCommand(
            "Updated Name",
            "Updated Description",
            "newImageUrl",
            0,
            PUBLIC);

        when(playlistRepository.update(updateCommand, SLUG_1)).thenReturn(PLAYLIST_1);

        PlaylistDTO updatedPlaylist = playlistService.updatePlaylist(updateCommand, SLUG_1);

        assertEquals(PLAYLIST_1, updatedPlaylist);
        verify(playlistRepository).update(any(UpdatePlaylistCommand.class), eq(SLUG_1));
    }

    @Test
    void updatePlaylist_UnknownSlug() {
        UpdatePlaylistCommand updateCommand = new UpdatePlaylistCommand(
            "Updated Name",
            "Updated Description",
            "newImageUrl",
            0,
            PUBLIC);

        when(playlistRepository.update(updateCommand, SLUG_1)).thenThrow(new UnknownSlugException("Slug not found"));

        assertThrows(PlaylistUnityException.class, () -> {
            playlistService.updatePlaylist(updateCommand, SLUG_1);
        });
    }

    @Test
    void retrieveAllPlaylists_Success() {
        // GIVEN
        when(playlistRepository.findAll()).thenReturn(PLAYLISTS);

        // WHEN
        List<PlaylistDTO> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists);
    }

    @Test
    void retrieveAllPlaylists_NoContent() {
        // GIVEN
        when(playlistRepository.findAll()).thenReturn(emptyList());

        // WHEN
        List<PlaylistDTO> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertTrue(retrievedPlaylists.isEmpty());
    }

    @Test
    void retrievePlaylists_NoFilter() {
        // GIVEN
        PlaylistFilter noFilter = new PlaylistFilter(empty());

        when(playlistRepository.findAll()).thenReturn(PLAYLISTS);

        // WHEN
        List<PlaylistDTO> retrievedPlaylists = playlistService.retrievePlaylists(noFilter);

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists);
    }

    @Test
    void retrievePlaylist_FilterUserSlug() {
        // GIVEN
        PlaylistFilter filter = new PlaylistFilter(of(USER.slug()));

        when(userRepository.retrievePlaylists(any())).thenReturn(List.of(PLAYLIST_1, PLAYLIST_3));

        // WHEN
        List<PlaylistDTO> retrievedPlaylist = playlistService.retrievePlaylists(filter);

        // THEN
        assertEquals(2, retrievedPlaylist.size());
        assertEquals(PLAYLIST_1, retrievedPlaylist.get(0));
        assertEquals(PLAYLIST_3, retrievedPlaylist.get(1));
    }

    @Test
    void retrievePlaylistBySlug_Unknown() {
        // GIVEN
        String unknownSlug = "unknown";

        when(playlistRepository.findBySlug(unknownSlug)).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> playlistService.retrievePlaylistBySlug(unknownSlug);

        // THEN
        assertThrows(UnknownSlugException.class, executable); // Change expected exception here
    }

    @Test
    void deletePlaylistBySlug_Success() {
        // GIVEN
        doNothing().when(playlistRepository).deletePlaylistBySlug(PLAYLIST_1.slug());

        // WHEN
        Executable executable = () -> playlistService.deletePlaylistBySlug(PLAYLIST_1.slug());

        // THEN
        assertDoesNotThrow(executable);
        verify(playlistRepository).deletePlaylistBySlug(PLAYLIST_1.slug());
    }

    @Test
    void deletePlaylistBySlug_Unknown() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(playlistRepository).deletePlaylistBySlug(PLAYLIST_1.slug());

        // WHEN
        Executable executable = () -> playlistService.deletePlaylistBySlug(PLAYLIST_1.slug());

        // THEN
        assertThrows(UnknownPlaylistException.class, executable);
    }

}
