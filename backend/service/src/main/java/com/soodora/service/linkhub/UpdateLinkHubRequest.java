package com.soodora.service.linkhub;

import com.soodora.domain.linkhub.UpdateLinkHubCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateLinkHubRequest(
        @Schema(description = "Title of the Link Hub", example = "My Link Hub", defaultValue = "My Link Hub") String title,
        @Schema(description = "Description of the Link Hub", example = "My Link Hub Description", defaultValue = "My Link Hub Description") String description)
        implements
            Request<UpdateLinkHubCommand> {

    @Override
    public UpdateLinkHubCommand toCommand() {
        if (!isStringValid(title, 255, true))
            throw new InvalidRequestException("Title cannot exceed 255 characters");
        if (!isStringValid(description, 255, true))
            throw new InvalidRequestException("Description cannot exceed 255 characters");

        return new UpdateLinkHubCommand(title.trim(), description.trim());
    }
}
