package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.CreateMediaKitCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateMediaKitRequest(
        @Schema(description = "Title of the media kit", example = "My MediaKit", defaultValue = "My MediaKit") String title,
        @Schema(description = "A short bio", example = "Get all my info !", defaultValue = "Get all my info !") String shortBio,
        @Schema(description = "A long bio that introduce the artist", example = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit", defaultValue = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit") String longBio,
        @Schema(description = "Email for contact", example = "mail@contact.com", defaultValue = "mail@contact.com") String contactEmail,
        @Schema(description = "Phone number for contact", example = "+3366062636465", defaultValue = "+3366062636465") String contactPhone)
        implements
            Request<CreateMediaKitCommand> {

    @Override
    public CreateMediaKitCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (shortBio == null || shortBio.isEmpty())
            throw new InvalidRequestException("Short Bio is not valid");
        if (longBio == null || longBio.isEmpty())
            throw new InvalidRequestException("Long Bio is not valid");
        if (contactEmail == null || contactEmail.isEmpty())
            throw new InvalidRequestException("Contact Email is not valid");
        if (contactPhone == null || contactPhone.isEmpty())
            throw new InvalidRequestException("Contact Phone is not valid");

        return new CreateMediaKitCommand(title.trim(), shortBio.trim(), longBio.trim(), contactEmail.trim(),
                contactPhone.trim());
    }
}
