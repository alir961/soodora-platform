package com.soodora.infrastructure.user;

import com.soodora.test.factory.UserFactory;

import java.util.Date;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
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
                emptyList(),
                emptyList(),
                null,
                emptySet(),
                emptySet(),
                emptySet());
    }

}
