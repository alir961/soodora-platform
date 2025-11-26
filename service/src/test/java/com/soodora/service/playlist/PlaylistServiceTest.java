package com.soodora.service.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.post.Post;
import com.soodora.service.post.UnknownPostException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
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
import static org.springframework.data.domain.Pageable.unpaged;

import static com.soodora.domain.factory.PlaylistFactory.buildDefaultPrivatePlaylist;
import static com.soodora.domain.factory.PlaylistFactory.buildDefaultPublicPlaylist;
import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @InjectMocks
    private PlaylistService playlistService;

    private static final String SLUG_1 = "slug1";
    private static final Playlist PLAYLIST_1 = buildDefaultPublicPlaylist(SLUG_1, "Name 1");
    private static final Playlist PLAYLIST_2 = buildDefaultPublicPlaylist("slug2", "Name 2");
    private static final Playlist PLAYLIST_3 = buildDefaultPublicPlaylist("slug3", "Name 3");
    private static final Playlist PRIVATE_PLAYLIST = buildDefaultPrivatePlaylist(SLUG_1, "Private Playlist");
    private static final List<Playlist> PLAYLISTS = List.of(PLAYLIST_1, PLAYLIST_2, PLAYLIST_3);
    private static final FileData FILE_DATA = new FileData(
            "dummy image content".getBytes(),
            "test-image.jpg",
            "image/jpeg");

    private static final UpdatePlaylistCommand UPDATE_COMMAND = new UpdatePlaylistCommand("Updated Name",
            "Updated Description", PUBLIC);

    @Test
    void givenValidCreatePlaylistCommand_whenCreatePlaylist_thenPlaylistIsSaved() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("New Playlist", "Description", PUBLIC);

        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.getSlug()))
                .thenReturn(false);
        when(playlistRepository.save(any(), eq(USER), eq(FILE_DATA)))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist createdPlaylist = playlistService.createPlaylist(USER, command, FILE_DATA);

        // THEN
        assertNotNull(createdPlaylist);
        verify(playlistRepository).save(any(), eq(USER), eq(FILE_DATA));
    }

    @Test
    void givenPlaylistNameAlreadyUsed_whenCreatePlaylist_thenThrowsPlaylistUnityException() {
        // GIVEN
        CreatePlaylistCommand command = new CreatePlaylistCommand("Existing Playlist", "Description", PUBLIC);

        when(playlistRepository.isPlaylistNameUsedByUser(command.name(), USER.getSlug()))
                .thenReturn(true);

        // WHEN
        Executable executable = () -> playlistService.createPlaylist(USER, command, FILE_DATA);

        // THEN
        assertThrows(PlaylistUnityException.class, executable, "Name is already used");
    }

    @Test
    void givenPlaylistsExist_whenRetrieveAllPlaylists_thenReturnAllPlaylists() {
        // GIVEN
        when(playlistRepository.findAll())
                .thenReturn(PLAYLISTS);

        // WHEN
        List<Playlist> retrievedPlaylists = playlistService.retrieveAllPlaylists();

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists);
    }

    @Test
    void givenNoPlaylistsExist_whenRetrieveAllPlaylists_thenReturnEmptyList() {
        // GIVEN
        when(playlistRepository.findAll())
                .thenReturn(emptyList());

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

        when(playlistRepository.findByFilter(eq(noFilter), any(Pageable.class)))
                .thenReturn(page);

        // WHEN
        Page<Playlist> retrievedPlaylists = playlistService.retrievePlaylists(noFilter, unpaged());

        // THEN
        assertEquals(PLAYLISTS, retrievedPlaylists.getContent());
    }

    @Test
    void givenPlaylistOwner_whenRetrievePlaylistBySlug_thenReturnFullPlaylist() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PRIVATE_PLAYLIST);

        // WHEN
        Playlist result = playlistService.retrievePlaylistBySlug(ARTIST_USER, SLUG_1);

        // THEN
        assertEquals(PRIVATE_PLAYLIST, result);
    }

    @Test
    void givenAdministrator_whenRetrievePlaylistBySlug_thenReturnFullPlaylist() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PRIVATE_PLAYLIST);

        // WHEN
        Playlist result = playlistService.retrievePlaylistBySlug(ADMIN, SLUG_1);

        // THEN
        assertEquals(PRIVATE_PLAYLIST, result);
    }

    @Test
    void givenPublicPlaylist_whenRetrievePlaylistBySlug_thenReturnFullPlaylist() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.retrievePlaylistBySlug(USER, SLUG_1);

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenPrivatePlaylist_whenRetrievePlaylistBySlug_thenThrowInvalidPermissionException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PRIVATE_PLAYLIST);

        // WHEN
        Executable executable = () -> playlistService.retrievePlaylistBySlug(USER, SLUG_1);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the playlist owner can access the playlist");
    }

    @Test
    void givenUnknownPlaylistSlug_whenRetrievePlaylistBySlug_thenThrowUnknownPlaylistException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenThrow(new UnknownSlugException("Not found"));

        // WHEN
        Executable executable = () -> playlistService.retrievePlaylistBySlug(USER, SLUG_1);

        // THEN
        assertThrows(UnknownPlaylistException.class, executable);
    }

    @Test
    void givenPlaylistOwner_whenRetrievePlaylistPostsBySlug_thenReturnPosts() {
        // GIVEN
        Page<Post> page = new PageImpl<>(PRIVATE_PLAYLIST.getPosts());

        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PRIVATE_PLAYLIST);
        when(playlistRepository.findPlaylistPosts(SLUG_1, unpaged()))
                .thenReturn(page);

        // WHEN
        Page<Post> result = playlistService.retrievePlaylistPostsBySlug(ARTIST_USER, SLUG_1, unpaged());

        // THEN
        assertEquals(PRIVATE_PLAYLIST.getPosts(), result.getContent());
    }

    @Test
    void givenPlaylistAdministrator_whenRetrievePlaylistPostsBySlug_thenReturnPosts() {
        // GIVEN
        Page<Post> page = new PageImpl<>(PRIVATE_PLAYLIST.getPosts());

        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PRIVATE_PLAYLIST);
        when(playlistRepository.findPlaylistPosts(SLUG_1, unpaged()))
                .thenReturn(page);

        // WHEN
        Page<Post> result = playlistService.retrievePlaylistPostsBySlug(ADMIN, SLUG_1, unpaged());

        // THEN
        assertEquals(PRIVATE_PLAYLIST.getPosts(), result.getContent());
    }

    @Test
    void givenPublicPlaylist_whenRetrievePlaylistPostsBySlug_thenReturnPosts() {
        // GIVEN
        Page<Post> page = new PageImpl<>(PRIVATE_PLAYLIST.getPosts());

        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.findPlaylistPosts(SLUG_1, unpaged()))
                .thenReturn(page);

        // WHEN
        Page<Post> result = playlistService.retrievePlaylistPostsBySlug(USER, SLUG_1, unpaged());

        // THEN
        assertEquals(PRIVATE_PLAYLIST.getPosts(), result.getContent());
    }

    @Test
    void givenPrivatePlaylist_whenRetrievePlaylistPostsBySlug_thenReturnPosts() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PRIVATE_PLAYLIST);

        // WHEN
        Executable executable = () -> playlistService.retrievePlaylistPostsBySlug(USER, SLUG_1, unpaged());

        // THEN
        assertThrows(InvalidPermissionException.class, executable,
                "Only the playlist owner can access the playlist posts");
    }

    @Test
    void givenPlaylistOwner_whenUpdatePlaylist_thenPlaylistIsUpdated() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.update(UPDATE_COMMAND, SLUG_1, FILE_DATA))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist updatedPlaylist = playlistService.updatePlaylist(ARTIST_USER, SLUG_1, UPDATE_COMMAND, FILE_DATA);

        // THEN
        assertEquals(PLAYLIST_1, updatedPlaylist);
    }

    @Test
    void givenAdministrator_whenUpdatePlaylist_thenPlaylistIsUpdated() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.update(UPDATE_COMMAND, SLUG_1, FILE_DATA))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist updatedPlaylist = playlistService.updatePlaylist(ADMIN, SLUG_1, UPDATE_COMMAND, FILE_DATA);

        // THEN
        assertEquals(PLAYLIST_1, updatedPlaylist);
    }

    @Test
    void givenInvalidUser_whenUpdatePlaylist_thenPlaylistIsUpdated() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Executable executable = () -> playlistService.updatePlaylist(USER, SLUG_1, UPDATE_COMMAND, FILE_DATA);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the playlist owner can update the playlist");
    }

    @Test
    void givenUnknownSlug_whenUpdatePlaylist_thenThrowsUnknownPlaylistException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenThrow(new UnknownSlugException("Not found"));

        // WHEN
        Executable executable = () -> playlistService.updatePlaylist(ARTIST_USER, SLUG_1, UPDATE_COMMAND, FILE_DATA);

        // WHEN
        assertThrows(UnknownPlaylistException.class, executable);
    }

    @Test
    void givenPlaylistOwner_whenDeletePlaylist_thenDeletePlaylist() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylist(ARTIST_USER, SLUG_1);

        // THEN
        assertDoesNotThrow(executable);
    }

    @Test
    void givenAdministrator_whenDeletePlaylist_thenDeletePlaylist() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylist(ADMIN, SLUG_1);

        // THEN
        assertDoesNotThrow(executable);
    }

    @Test
    void givenInvalidUser_whenDeletePlaylist_thenThrowsInvalidPermissionException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylist(USER, SLUG_1);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the playlist owner can delete the playlist");
    }

    @Test
    void givenUnknownPlaylistSlug_whenDeletePlaylist_thenThrowsUnknownPlaylistException() {
        // GIVEN
        doThrow(new UnknownSlugException("Not found"))
                .when(playlistRepository).findBySlug(SLUG_1);

        // WHEN
        Executable executable = () -> playlistService.deletePlaylist(ARTIST_USER, SLUG_1);

        // THEN
        assertThrows(UnknownPlaylistException.class, executable);
    }

    @Test
    void givenPlaylistOwner_whenAddPostToPlaylist_thenPostIsAdded() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.addPostToPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.addPostToPlaylist(ARTIST_USER, SLUG_1, "postSlug1");

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenAdministrator_whenAddPostToPlaylist_thenPostIsAdded() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.addPostToPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.addPostToPlaylist(ADMIN, SLUG_1, "postSlug1");

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenInvalidUser_whenAddPostToPlaylist_thenPostIsAdded() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Executable executable = () -> playlistService.addPostToPlaylist(USER, SLUG_1, "postSlug1");

        // THEN
        assertThrows(InvalidPermissionException.class, executable,
                "Only the playlist owner can add a post to the playlist");
    }

    @Test
    void givenUnknownPostSlug_whenAddPostToPlaylist_thenThrowsUnknownPostException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.addPostToPlaylist(PLAYLIST_1, "postSlug1"))
                .thenThrow(new UnknownSlugException("Not found"));

        // WHEN
        Executable executable = () -> playlistService.addPostToPlaylist(ARTIST_USER, SLUG_1, "postSlug1");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenPlaylistOwner_whenRemovePostFromPlaylist_thenPostIsRemoved() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.removePostFromPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.removePostFromPlaylist(ARTIST_USER, SLUG_1, "postSlug1");

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenAdministrator_whenRemovePostFromPlaylist_thenPostIsRemoved() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.removePostFromPlaylist(PLAYLIST_1, "postSlug1"))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Playlist result = playlistService.removePostFromPlaylist(ADMIN, SLUG_1, "postSlug1");

        // THEN
        assertEquals(PLAYLIST_1, result);
    }

    @Test
    void givenInvalidUser_whenRemovePostFromPlaylist_thenPostIsRemoved() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);

        // WHEN
        Executable executable = () -> playlistService.removePostFromPlaylist(USER, SLUG_1, "postSlug1");

        // THEN
        assertThrows(InvalidPermissionException.class, executable,
                "Only the playlist owner can delete a post from the playlist");
    }

    @Test
    void givenUnknownPostSlug_whenRemovePostFromPlaylist_thenThrowsPlaylistUnityException() {
        // GIVEN
        when(playlistRepository.findBySlug(SLUG_1))
                .thenReturn(PLAYLIST_1);
        when(playlistRepository.removePostFromPlaylist(PLAYLIST_1, "postSlug1"))
                .thenThrow(new UnknownSlugException("Not found"));

        // WHEN
        Executable executable = () -> playlistService.removePostFromPlaylist(ARTIST_USER, SLUG_1, "postSlug1");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

}
