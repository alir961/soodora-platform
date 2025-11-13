package com.soodora.api.user;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.InvalidValueException;
import com.soodora.service.common.Request;
import com.soodora.service.user.UserUpdateCommand;

public record UpdateUserRequest(
        String username,
        String firstname,
        String lastname,
        String description,
        String avatarUrl) implements Request<UserUpdateCommand> {

    @Override
    public UserUpdateCommand toCommand() throws InvalidValueException {
        return toCommand(null);
    }

    public UserUpdateCommand toCommand(FileData avatar) {
        return new UserUpdateCommand(
                username,
                firstname,
                lastname,
                description,
                avatar);
    }

}
