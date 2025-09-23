package com.soodora.service.link;

import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateLinkRequest(
        @Schema(description = "Link Title", example = "My Link", defaultValue = "My Link") String title,
        @Schema(description = "Description of the Link", example = "My instagram Link", defaultValue = "My instagram Link") String description,
        @Schema(description = "The Link", example = "instagram.com", defaultValue = "instagram.com") String url,
        @Schema(description = "Icon of the Link", example = "iconurl.com/icon", defaultValue = "iconurl.com/icon") String iconUrl)
        implements
            Request<UpdateLinkCommand> {

    @Override
    public UpdateLinkCommand toCommand() {
        if (!isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot exceed 255 characters");
        if (!isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters");
        if (!isStringValid(url, 5000))
            throw new InvalidRequestException("Link URL cannot exceed 5000 characters");
        if (!isStringValid(iconUrl, 5000))
            throw new InvalidRequestException("Icon URL cannot exceed 5000 characters");

        return new UpdateLinkCommand(title, description, url, iconUrl);
    }

}
