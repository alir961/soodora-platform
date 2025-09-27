package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.UpdateMediaKitCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateMediaKitRequest(
        @Schema(description = "Title of the media kit", example = "My MediaKit", defaultValue = "My MediaKit") String title,
        @Schema(description = "A short bio", example = "Get all my info !", defaultValue = "Get all my info !") String shortBio,
        @Schema(description = "A long bio that introduce the artist", example = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit", defaultValue = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit") String longBio,
        @Schema(description = "Email for contact", example = "mail@contact.com", defaultValue = "mail@contact.com") String contactEmail,
        @Schema(description = "Phone number for contact", example = "+3366062636465", defaultValue = "+3366062636465") String contactPhone)
        implements
            Request<UpdateMediaKitCommand> {

    @Override
    public UpdateMediaKitCommand toCommand() {
        if (!isStringValid(title, 255, true))
            throw new InvalidRequestException("Title cannot exceed 255 characters");
        if (!isStringValid(contactEmail, 255, true))
            throw new InvalidRequestException("Contact Email cannot exceed 255 characters");
        if (!isStringValid(contactPhone, 50, true))
            throw new InvalidRequestException("Contact Phone cannot exceed 50 characters");

        return new UpdateMediaKitCommand(title.trim(), shortBio.trim(), longBio.trim(), contactEmail.trim(),
                contactPhone.trim());
    }
}
