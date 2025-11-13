package com.soodora.service.link;

import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateLink;

public record CreateLinkRequest(
        @Schema(description = "Link Title", example = "My Link", defaultValue = "My Link") String title,
        @Schema(description = "Description of the Link", example = "My instagram Link", defaultValue = "My instagram Link") String description,
        @Schema(description = "The Link", example = "instagram.com", defaultValue = "instagram.com") String url,
        @Schema(description = "Icon of the Link", example = "iconurl.com/icon", defaultValue = "iconurl.com/icon") String iconUrl)
        implements
            Request<CreateLinkCommand> {

    @Override
    public CreateLinkCommand toCommand() {
        validateLink(title, description, url, iconUrl);

        return new CreateLinkCommand(title.trim(), description.trim(), url, iconUrl);
    }
}
