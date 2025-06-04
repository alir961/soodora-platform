package com.grooveconductor.service.user;

import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

public record UpdateUserRequest(
        String username,
        String firstname,
        String lastname,
        String avatarUrl) implements Request<UpdateUserCommand> {

    @Override
    public UpdateUserCommand toCommand() throws InvalidRequestException {
        return new UpdateUserCommand(
                username,
                firstname,
                lastname,
                avatarUrl);
    }

}
