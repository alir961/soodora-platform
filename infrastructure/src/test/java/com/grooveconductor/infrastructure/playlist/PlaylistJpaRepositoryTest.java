package com.grooveconductor.infrastructure.playlist;

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

import static com.grooveconductor.infrastructure.playlist.PlaylistEntityFactory.buildDefaultPlaylistEntity;
import static com.grooveconductor.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfrastructureTestConfiguration.class)
class PlaylistJpaRepositoryTest {

    @Autowired
    private PlaylistJpaRepository playlistRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private UserEntity userEntity;
    private PlaylistEntity playlistEntity;

    @BeforeEach
    void setUp() {
        userEntity = buildDefaultUserEntity();
        userEntity = userRepository.save(userEntity);

        playlistEntity = buildDefaultPlaylistEntity(userEntity);
        playlistRepository.save(playlistEntity);
    }

    @Test
    void testFindBySlug_PlaylistExists() {
        // WHEN
        Optional<PlaylistEntity> foundPlaylist = playlistRepository.findBySlug("slug");

        // THEN
        assertTrue(foundPlaylist.isPresent());
        assertEquals("slug", foundPlaylist.get().getSlug());
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
        // WHEN
        Optional<List<PlaylistEntity>> foundPlaylists = playlistRepository.findPlaylistsByUserSlug("slug");

        // THEN
        assertTrue(foundPlaylists.isPresent());
        assertFalse(foundPlaylists.get().isEmpty());
    }

    @Test
    void testFindPlaylistsByUserSlug_PlaylistsDoNotExist() {
        // WHEN
        Optional<List<PlaylistEntity>> foundPlaylists = playlistRepository.findPlaylistsByUserSlug("unknownUserSlug");

        // THEN
        assertTrue(foundPlaylists.isPresent());
        assertTrue(foundPlaylists.get().isEmpty());
    }

    @Test
    void testSavePlaylist() {
        // GIVEN
        PlaylistEntity newPlaylist = buildDefaultPlaylistEntity(userEntity);

        newPlaylist.setSlug("new-slug");

        // WHEN
        PlaylistEntity savedPlaylist = playlistRepository.save(newPlaylist);

        // THEN
        assertNotNull(savedPlaylist.getId());
        assertEquals("new-slug", savedPlaylist.getSlug());
    }

    @Test
    void testDeletePlaylist() {
        // WHEN
        playlistRepository.delete(playlistEntity);

        // THEN
        assertFalse(playlistRepository.existsById(playlistEntity.getId()));
    }

}
