package com.grooveconductor.service.artist;

import com.grooveconductor.domain.artist.CreateArtistCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateArtistRequest(
        @Schema(description = "Name of the artist", example = "Artist", defaultValue = "Artist") String name,
        @Schema(description = "Description of the artist", example = "Welcome to my musical world!") String description,
        @Schema(description = "Image URL of the artist", example = "https://artist.com/image.jpg") String imageUrl)
        implements
        Request<CreateArtistCommand> {

    @Override
    public CreateArtistCommand toCommand() throws InvalidRequestException {
        if (name == null || name.trim().isBlank())
            throw new InvalidRequestException("Invalid name");

        return new CreateArtistCommand(name.trim(), description.trim(), imageUrl.trim());
    }

}
