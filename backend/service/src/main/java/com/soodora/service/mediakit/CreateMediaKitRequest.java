package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.CreateMediaKitCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;

import static com.soodora.service.validation.ValidationUtils.validateMediaKit;

public record CreateMediaKitRequest(
        @Schema(description = "Title of the media kit", example = "My MediaKit", defaultValue = "My MediaKit") String title,
        @Schema(description = "A short bio", example = "Get all my info !", defaultValue = "Get all my info !") String shortBio,
        @Schema(description = "A long bio that introduce the artist", example = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit", defaultValue = "I'm a House DJ, I'm inspire by Solomun and there is my Soodora MediaKit") String longBio,
        @Schema(description = "Email for contact", example = "mail@contact.com", defaultValue = "mail@contact.com") @Email String contactEmail,
        @Schema(description = "Phone number for contact", example = "+33600000000", defaultValue = "+33600000000") String contactPhone)
        implements
            Request<CreateMediaKitCommand> {

    @Override
    public CreateMediaKitCommand toCommand() {
        validateMediaKit(title, shortBio, longBio, contactEmail, contactPhone);

        return new CreateMediaKitCommand(title.trim(), shortBio.trim(), longBio.trim(), contactEmail.trim(),
                contactPhone.trim());
    }
}
