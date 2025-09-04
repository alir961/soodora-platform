package com.soodora.service.mediakit;

import com.soodora.domain.mediakititem.CreateMediaKitItemCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateMediaKitItemRequest(
        @Schema(description = "Type of the item", example = "Image", defaultValue = "Image") String type,
        @Schema(description = "Title of the item", example = "My Item", defaultValue = "My Item") String title,
        @Schema(description = "Content of the item", example = "imageUrl", defaultValue = "imageUrl") String content)
        implements
            Request<CreateMediaKitItemCommand> {

    @Override
    public CreateMediaKitItemCommand toCommand() {
        if (type == null || type.isEmpty())
            throw new InvalidRequestException("type is not valid");
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (content == null || content.isEmpty())
            throw new InvalidRequestException("Content is not valid");

        return new CreateMediaKitItemCommand(type.trim(), title.trim(), content.trim());
    }
}
