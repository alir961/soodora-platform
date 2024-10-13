package com.grooveconductor.infrastructure.playlist;

import com.grooveconductor.domain.playlist.PlaylistPrivacy;
import com.grooveconductor.infrastructure.user.UserJpaRepository;
import com.grooveconductor.infrastructure.InfrastructureTestConfiguration;
import com.grooveconductor.infrastructure.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfrastructureTestConfiguration.class)
class PlaylistJpaRepositoryTest {

    @Autowired
    private PlaylistJpaRepository playlistRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private PlaylistEntity playlistEntity;

    @BeforeEach
    void setUp() {
        playlistEntity = new PlaylistEntity();
        playlistEntity.setId(randomUUID());
        playlistEntity.setSlug("slug1");
        playlistEntity.setName("Playlist 1");
        playlistEntity.setDescription("Description 1");
        playlistEntity.setState(PlaylistPrivacy.valueOf("PUBLIC"));
        playlistEntity.setImageUrl("imageUrl1");

        playlistRepository.save(playlistEntity);
    }

    @Test
    void testFindBySlug_PlaylistExists() {
        // WHEN
        Optional<PlaylistEntity> foundPlaylist = playlistRepository.findBySlug("slug1");

        // THEN
        assertTrue(foundPlaylist.isPresent());
        assertEquals("slug1", foundPlaylist.get().getSlug());
    }

    @Test
    void testFindBySlug_PlaylistDoesNotExist() {
        // WHEN
        Optional<PlaylistEntity> foundPlaylist = playlistRepository.findBySlug("unknownSlug");

        // THEN
        assertFalse(foundPlaylist.isPresent());
    }

    @Test
    void testFindPlaylistsByUserSlug_PlaylistsExist() {
        UserEntity user = new UserEntity();
        user.setSlug("user-slug");
        user.setUsername("username");
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstname("John");
        user.setLastname("Doe");
        user = userRepository.save(user);

        PlaylistEntity playlist = new PlaylistEntity();
        playlist.setSlug("playlist-slug");
        playlist.setName("My Playlist");
        playlist.setDescription("A test playlist");
        playlist.setState(PlaylistPrivacy.PUBLIC);
        playlist.setUser(user);
        playlistRepository.save(playlist);

        // WHEN
        Optional<List<PlaylistEntity>> foundPlaylists = playlistRepository.findPlaylistsByUserSlug("user-slug");

        // THEN
        assertTrue(foundPlaylists.isPresent());
        assertFalse(foundPlaylists.get().isEmpty());
    }

    @Test
    void testFindPlaylistsByUserSlug_PlaylistsDoNotExist() {
        // WHEN
        Optional<List<PlaylistEntity>> foundPlaylists = playlistRepository.findPlaylistsByUserSlug("unknownUserSlug");

        // THEN
        assertTrue(foundPlaylists.get().isEmpty());
    }

    @Test
    void testSavePlaylist() {
        // GIVEN
        PlaylistEntity newPlaylist = new PlaylistEntity();
        newPlaylist.setId(randomUUID());
        newPlaylist.setSlug("newslug");
        newPlaylist.setName("New Playlist");
        newPlaylist.setDescription("New Description");
        newPlaylist.setState(PlaylistPrivacy.valueOf("PRIVATE"));
        newPlaylist.setImageUrl("newImageUrl");

        // WHEN
        PlaylistEntity savedPlaylist = playlistRepository.save(newPlaylist);

        // THEN
        assertNotNull(savedPlaylist.getId());
        assertEquals("newslug", savedPlaylist.getSlug());
        assertEquals("New Playlist", savedPlaylist.getName());
    }

    @Test
    void testDeletePlaylist() {
        // WHEN
        playlistRepository.delete(playlistEntity);

        // THEN
        Optional<PlaylistEntity> foundPlaylist = playlistRepository.findById(playlistEntity.getId());
        assertFalse(foundPlaylist.isPresent());
    }

}
