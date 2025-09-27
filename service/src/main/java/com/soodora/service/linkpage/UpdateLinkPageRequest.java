package com.soodora.service.linkpage;

import com.soodora.domain.linkpage.UpdateLinkPageCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateLinkPageRequest(
        @Schema(description = "Title of the Link Page", example = "My Link Page", defaultValue = "My Link Page") String title,
        @Schema(description = "Description of the Link Page", example = "My Link Page Description", defaultValue = "My Link Page Description") String description)
        implements
            Request<UpdateLinkPageCommand> {

    @Override
    public UpdateLinkPageCommand toCommand() {
        if (!isStringValid(title, 255, true))
            throw new InvalidRequestException("Title cannot exceed 255 characters");
        if (!isStringValid(description, 255, true))
            throw new InvalidRequestException("Description cannot exceed 255 characters");

        return new UpdateLinkPageCommand(title.trim(), description.trim());
    }
}
