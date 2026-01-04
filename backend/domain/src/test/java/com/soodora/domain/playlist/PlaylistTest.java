package com.soodora.domain.playlist;

import com.soodora.domain.user.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static com.soodora.domain.factory.PlaylistFactory.buildDefaultPublicPlaylist;
import static com.soodora.domain.factory.UserFactory.*;

class PlaylistTest {

    @Test
    void givenPlaylistOwner_whenHasAccess_thenHasAccess() {
        // GIVEN
        Playlist playlist = buildDefaultPublicPlaylist("playlist-slug", "Playlist");
        User owner = buildDefaultArtistUser();

        // WHEN
        boolean hasAccess = playlist.hasAccess(owner);

        // THEN
        assertTrue(hasAccess);
    }

    @Test
    void givenAdmin_whenHasAccess_thenHasAccess() {
        // GIVEN
        Playlist playlist = buildDefaultPublicPlaylist("playlist-slug", "Playlist");
        User owner = buildDefaultAdminUser();

        // WHEN
        boolean hasAccess = playlist.hasAccess(owner);

        // THEN
        assertTrue(hasAccess);
    }

    @Test
    void givenUser_whenHasAccess_thenHasNotAccess() {
        // GIVEN
        Playlist playlist = buildDefaultPublicPlaylist("playlist-slug", "Playlist");
        User owner = buildDefaultUser();

        // WHEN
        boolean hasAccess = playlist.hasAccess(owner);

        // THEN
        assertFalse(hasAccess);
    }

}
