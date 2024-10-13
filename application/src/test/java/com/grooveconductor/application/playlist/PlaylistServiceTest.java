package com.grooveconductor.application.playlist;

import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.domain.playlist.CreatePlaylistCommand;
import com.grooveconductor.domain.playlist.PlaylistDto;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.playlist.PlaylistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.grooveconductor.test.PlaylistFactory.buildDefaultPlaylist;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;
    private PlaylistService playlistService;

    private static final PlaylistDto PLAYLIST = buildDefaultPlaylist("PlaylistSlug", "PlaylistName1");
    private static final List<PlaylistDto> PLAYLISTS = List.of(PLAYLIST);
    private static final UserDTO USER = buildDefaultUser();

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService(playlistRepository);
    }

    @Test
    void retrieveAllPlaylists_Success() {
        // GIVEN
        when(playlistRepository.findAll()).thenReturn(PLAYLISTS);

        // WHEN
        List<PlaylistDto> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists);
    }

    @Test
    void retrieveAllPlaylists_NoContent() {
        // GIVEN
        when(playlistRepository.findAll()).thenReturn(emptyList());

        // WHEN
        List<PlaylistDto> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertTrue(retrievedPlaylists.isEmpty());
    }

    @Test
    void retrievePlaylistBySlug_Success() {
        // GIVEN
        String slug = PLAYLIST.slug();
        when(playlistRepository.findBySlug(slug)).thenReturn(PLAYLIST);

        // WHEN
        PlaylistDto retrievedPlaylist = playlistService.retrievePlaylistBySlug(slug);

        // THEN
        assertEquals(PLAYLIST, retrievedPlaylist);
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
        String slug = PLAYLIST.slug();
        doNothing().when(playlistRepository).deletePlaylistBySlug(slug);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylistBySlug(slug);

        // THEN
        assertDoesNotThrow(executable);
        verify(playlistRepository, times(1)).deletePlaylistBySlug(slug);
    }

    @Test
    void deletePlaylistBySlug_Unknown() {
        // GIVEN
        String slug = PLAYLIST.slug();
        doThrow(UnknownSlugException.class).when(playlistRepository).deletePlaylistBySlug(slug);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylistBySlug(slug);

        // THEN
        assertThrows(UnknownSlugException.class, executable);
    }

    @Test
    void createPlaylist_Success() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("New Playlist", "Description", PlaylistPrivacy.PUBLIC, "imageUrl");
        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.slug())).thenReturn(false);
        when(playlistRepository.save(any(), eq(USER))).thenReturn(PLAYLIST);

        // WHEN
        playlistService.createPlaylist(command, USER);

        // THEN
        verify(playlistRepository, times(1)).save(any(), eq(USER));
    }

    @Test
    void createPlaylist_NameAlreadyUsed() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("Existing Playlist", "Description", PlaylistPrivacy.PUBLIC, "imageUrl");
        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.slug())).thenReturn(true);

        // WHEN
        Executable executable = () -> playlistService.createPlaylist(command, USER);

        // THEN
        assertThrows(PlaylistUnityException.class, executable);
    }

}
