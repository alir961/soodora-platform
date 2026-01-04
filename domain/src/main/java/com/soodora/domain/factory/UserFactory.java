package com.soodora.domain.factory;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Email;
import com.soodora.domain.user.value.Username;

import java.util.Date;
import java.util.HashSet;

import lombok.NoArgsConstructor;

import static java.util.Set.of;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class UserFactory {

    public static User buildNewUser() {
        return new User(
                null,
                new Username("username"),
                new Email("user@example.com"),
                "password",
                "John",
                "Doe",
                new Date(),
                new Date(),
                "Description",
                null,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                true);
    }

    public static User buildDefaultUser() {
        return new User(
                "slug",
                new Username("username"),
                new Email("user@example.com"),
                "password",
                "John",
                "Doe",
                new Date(),
                new Date(),
                "Description",
                null,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                true);
    }

    public static User buildDefaultUser(int id) {
        return new User(
                "slug" + id,
                new Username("username" + id),
                new Email("user" + id + "@example.com"),
                "password",
                "John",
                "Doe",
                new Date(),
                new Date(),
                "Description",
                null,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                true);
    }

    public static User buildDefaultArtistUser() {
        return new User(
                "artist",
                new Username("username"),
                new Email("user@example.com"),
                "password",
                "John",
                "Doe",
                new Date(),
                new Date(),
                "Description",
                null,
                of(new Role("ARTIST")),
                new HashSet<>(),
                new HashSet<>(),
                true);
    }

    public static User buildDefaultAdminUser() {
        return new User(
                "admin",
                new Username("username"),
                new Email("user@example.com"),
                "password",
                "John",
                "Doe",
                new Date(),
                new Date(),
                "Description",
                null,
                of(new Role("ADMIN")),
                new HashSet<>(),
                new HashSet<>(),
                true);
    }

}
