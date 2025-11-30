package com.soodora.infrastructure.user;

import com.soodora.domain.factory.UserFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static java.util.UUID.randomUUID;

public class UserEntityFactory extends UserFactory {

    public static UserEntity buildDefaultUserEntity() {
        return new UserEntity(
                randomUUID(),
                "slug",
                "username",
                "user@example.com",
                "password",
                "John",
                "Doe",
                new Date(),
                new Date(),
                "Description",
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>());
    }

}
