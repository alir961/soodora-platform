package com.grooveconductor.request;

import com.grooveconductor.domain.command.CreateArtistCommand;
import com.grooveconductor.exception.InvalidRequestException;

public record CreateArtistRequest(String name, String description, String imageUrl) implements Request<CreateArtistCommand> {

    @Override
    public CreateArtistCommand toCommand() throws InvalidRequestException{
        if (name == null || name.trim().isBlank()) throw new InvalidRequestException("Invalid name");

        return new CreateArtistCommand(name.trim(), description.trim(), imageUrl.trim());
    }

}
