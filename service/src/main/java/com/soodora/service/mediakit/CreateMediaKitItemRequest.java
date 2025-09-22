package com.soodora.service.mediakit;

import com.soodora.domain.mediakititem.CreateMediaKitItemCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateMediaKitItem;

public record CreateMediaKitItemRequest(
        @Schema(description = "Type of the item", example = "Image", defaultValue = "Image") String type,
        @Schema(description = "Title of the item", example = "My Item", defaultValue = "My Item") String title,
        @Schema(description = "Content of the item", example = "imageUrl", defaultValue = "imageUrl") String content)
        implements
            Request<CreateMediaKitItemCommand> {

    @Override
    public CreateMediaKitItemCommand toCommand() {
        validateMediaKitItem(type, title, content);

        return new CreateMediaKitItemCommand(type.trim(), title.trim(), content.trim());
    }
}
