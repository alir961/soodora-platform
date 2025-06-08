package com.grooveconductor.test.factory;

import com.grooveconductor.domain.user.UserDTO;

import java.util.Set;

import lombok.NoArgsConstructor;

import static java.util.Collections.emptySet;
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
                emptySet(),
                null);
    }

    public static UserDTO buildDefaultUser(int id) {
        return new UserDTO(
                "slug" + id,
                "username" + id,
                "user" + id + "@example.com",
                "John",
                "Doe",
                emptySet(),
                null);
    }

    public static UserDTO buildDefaultArtistUser() {
        return new UserDTO(
                "slug",
                "username",
                "user@example.com",
                "John",
                "Doe",
                Set.of("ARTIST"),
                null);
    }

}
