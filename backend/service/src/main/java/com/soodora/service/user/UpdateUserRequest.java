package com.soodora.service.user;

import com.soodora.domain.user.UpdateUserCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record UpdateUserRequest(
        String username,
        String firstname,
        String lastname,
        String description) implements Request<UpdateUserCommand> {

    @Override
    public UpdateUserCommand toCommand() throws InvalidRequestException {
        return new UpdateUserCommand(
                username,
                firstname,
                lastname,
                description);
    }

}
