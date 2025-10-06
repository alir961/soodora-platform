package com.soodora.service.linkpage;

import com.soodora.domain.linkpage.CreateLinkPageCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateLinkPage;

public record CreateLinkPageRequest(
        @Schema(description = "Title of the Link Page", example = "My Link Page", defaultValue = "My Link Page") String title,
        @Schema(description = "Description of the Link Page", example = "My Link Page Description", defaultValue = "My Link Page Description") String description)
        implements
            Request<CreateLinkPageCommand> {

    @Override
    public CreateLinkPageCommand toCommand() {
        validateLinkPage(title, description);

        return new CreateLinkPageCommand(title.trim(), description.trim());
    }
}
