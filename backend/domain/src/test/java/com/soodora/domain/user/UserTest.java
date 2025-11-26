package com.soodora.domain.user;

import com.soodora.domain.auth.value.Role;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.*;

import static com.soodora.domain.factory.UserFactory.*;

class UserTest {

    @Test
    void givenUser_whenUpdateRoles_thenIsAdmin() {
        // GIVEN
        User user = buildDefaultUser();

        // WHEN
        user.updateRoles(Set.of(new Role("ADMIN")));

        // THEN
        assertTrue(user.isAdmin());
    }

    @Test
    void givenAdmin_whenUpdateRoles_thenIsNotAdmin() {
        // GIVEN
        User user = buildDefaultAdminUser();

        // WHEN
        user.updateRoles(emptySet());

        // THEN
        assertFalse(user.isAdmin());
    }

    @Test
    void givenUser_whenIsAdmin_thenIsNotAdmin() {
        // GIVEN
        User user = buildDefaultUser();

        // WHEN
        boolean isAdmin = user.isAdmin();

        // THEN
        assertFalse(isAdmin);
    }

    @Test
    void givenArtist_whenIsAdmin_thenIsNotAdmin() {
        // GIVEN
        User user = buildDefaultArtistUser();

        // WHEN
        boolean isAdmin = user.isAdmin();

        // THEN
        assertFalse(isAdmin);
    }

    @Test
    void givenAdmin_whenIsAdmin_thenIsAdmin() {
        // GIVEN
        User user = buildDefaultAdminUser();

        // WHEN
        boolean isAdmin = user.isAdmin();

        // THEN
        assertTrue(isAdmin);
    }

    @Test
    void givenAnotherUser_whenFollow_thenFollowed() {
        // GIVEN
        User follower = buildDefaultUser(1);
        User following = buildDefaultUser(2);

        // WHEN
        follower.follow(following);

        // THEN
        assertTrue(follower.isFollowing(following));
    }

    @Test
    void givenSameUser_whenFollow_thenThrowUserFollowException() {
        // GIVEN
        User user = buildDefaultUser();

        // WHEN
        Executable executable = () -> user.follow(user);

        // THEN
        assertThrows(UserFollowException.class, executable, "User cannot follow itself");
    }

    @Test
    void givenAnotherUser_whenUnfollow_thenUnfollowed() {
        // GIVEN
        User follower = buildDefaultUser(1);
        User following = buildDefaultUser(2);

        follower.follow(following);

        // WHEN
        follower.unfollow(following);

        // THEN
        assertFalse(follower.isFollowing(following));
    }

    @Test
    void givenSameUser_whenUnfollow_thenThrowUserFollowException() {
        // GIVEN
        User user = buildDefaultUser();

        // WHEN
        Executable executable = () -> user.unfollow(user);

        // THEN
        assertThrows(UserFollowException.class, executable, "User cannot follow itself");
    }

    @Test
    void givenFollowingUser_whenIsFollowing_thenIsFollowing() {
        // GIVEN
        User follower = buildDefaultUser(1);
        User following = buildDefaultUser(2);

        follower.follow(following);

        // WHEN
        boolean isFollowing = follower.isFollowing(following);

        // THEN
        assertTrue(isFollowing);
    }

    @Test
    void givenNotFollowingUser_whenIsFollowing_thenIsNotFollowing() {
        // GIVEN
        User follower = buildDefaultUser(1);
        User following = buildDefaultUser(2);

        // WHEN
        boolean isFollowing = follower.isFollowing(following);

        // THEN
        assertFalse(isFollowing);
    }

    @Test
    void givenSameUser_whenIsSame_thenIsSame() {
        // GIVEN
        User user1 = buildDefaultUser(1);
        User user2 = buildDefaultUser(1);

        // WHEN
        boolean isSame = user1.isSame(user2);

        // THEN
        assertTrue(isSame);
    }

    @Test
    void givenDifferentUser_whenIsSame_thenIsNotSame() {
        // GIVEN
        User user1 = buildDefaultUser(1);
        User user2 = buildDefaultUser(2);

        // WHEN
        boolean isSame = user1.isSame(user2);

        // THEN
        assertFalse(isSame);
    }

}
