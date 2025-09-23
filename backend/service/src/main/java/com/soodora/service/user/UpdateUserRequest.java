package com.soodora.service.user;

import com.soodora.domain.user.UpdateUserCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateUserRequest(
        String username,
        String firstname,
        String lastname,
        String description) implements Request<UpdateUserCommand> {

    @Override
    public UpdateUserCommand toCommand() throws InvalidRequestException {
        if (!isStringValid(username, 50))
            throw new InvalidRequestException("Username cannot exceed 50 characters");
        if (!isStringValid(firstname, 50))
            throw new InvalidRequestException("Firstname cannot exceed 50 characters");
        if (!isStringValid(lastname, 50))
            throw new InvalidRequestException("Lastname cannot exceed 50 characters");
        if (!isStringValid(description, 1000))
            throw new InvalidRequestException("Description cannot exceed 1000 characters");

        return new UpdateUserCommand(
                username,
                firstname,
                lastname,
                description);
    }

}
