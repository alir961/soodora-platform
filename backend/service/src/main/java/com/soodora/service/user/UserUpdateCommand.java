package com.soodora.service.user;

import com.soodora.domain.cdn.FileData;

public record UserUpdateCommand(
        String username,
        String firstname,
        String lastname,
        String description,
        FileData avatar) {
}
