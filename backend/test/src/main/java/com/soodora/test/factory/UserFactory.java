package com.soodora.test.factory;

import com.soodora.domain.user.UserDTO;

import java.util.Date;

import lombok.NoArgsConstructor;

import static java.util.Collections.emptySet;
import static java.util.Set.of;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class UserFactory {

    public static UserDTO buildDefaultUser() {
        return new UserDTO(
                "slug",
                "username",
                "user@example.com",
                "John",
                "Doe",
                "Description",
                null,
                new Date(),
                new Date(),
                emptySet());
    }

    public static UserDTO buildDefaultUser(int id) {
        return new UserDTO(
                "slug" + id,
                "username" + id,
                "user" + id + "@example.com",
                "John",
                "Doe",
                "Description",
                null,
                new Date(),
                new Date(),
                emptySet());
    }

    public static UserDTO buildDefaultArtistUser() {
        return new UserDTO(
                "slug",
                "username",
                "user@example.com",
                "John",
                "Doe",
                "Description",
                null,
                new Date(),
                new Date(),
                of("ARTIST"));
    }

    public static UserDTO buildDefaultAdminUser() {
        return new UserDTO(
                "slug",
                "username",
                "user@example.com",
                "John",
                "Doe",
                "Description",
                null,
                new Date(),
                new Date(),
                of("ADMIN"));
    }

}
