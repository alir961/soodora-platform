package com.grooveconductor.test;

import com.grooveconductor.domain.dto.UserDTO;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserFactory {

    public static UserDTO buildDefaultUser() {
        return new UserDTO("slug", "user", "user@example.com", "John", "Doe");
    }

    public static UserDTO buildDefaultUser(int id) {
        return new UserDTO("slug" + id, "user" + id, "user" + id + "@example.com", "John", "Doe");
    }

}
