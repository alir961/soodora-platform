package com.grooveconductor.application.artist;

import com.grooveconductor.domain.artist.CreateArtistCommand;
import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.common.Request;

public record CreateArtistRequest(
    String name,
    String description,
    String imageUrl
) implements Request<CreateArtistCommand> {

    @Override
    public CreateArtistCommand toCommand() throws InvalidRequestException{
        if (name == null || name.trim().isBlank()) throw new InvalidRequestException("Invalid name");

        return new CreateArtistCommand(name.trim(), description.trim(), imageUrl.trim());
    }

}
