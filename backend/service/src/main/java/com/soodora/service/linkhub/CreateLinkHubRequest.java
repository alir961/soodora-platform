package com.soodora.service.linkhub;

import com.soodora.domain.linkhub.CreateLinkHubCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateLinkHub;

public record CreateLinkHubRequest(
        @Schema(description = "Title of the Link Hub", example = "My Link Hub", defaultValue = "My Link Hub") String title,
        @Schema(description = "Description of the Link Hub", example = "My Link Hub Description", defaultValue = "My Link Hub Description") String description)
        implements
            Request<CreateLinkHubCommand> {

    @Override
    public CreateLinkHubCommand toCommand() {
        validateLinkHub(title, description);

        return new CreateLinkHubCommand(title.trim(), description.trim());
    }
}
