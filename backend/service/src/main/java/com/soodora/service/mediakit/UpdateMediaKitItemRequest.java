package com.soodora.service.mediakit;

import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateMediaKitItemRequest(
        @Schema(description = "Type of the item", example = "Image", defaultValue = "Image") String type,
        @Schema(description = "Title of the item", example = "My Item", defaultValue = "My Item") String title,
        @Schema(description = "Content of the item", example = "imageUrl", defaultValue = "imageUrl") String content)
        implements
            Request<UpdateMediaKitItemCommand> {

    @Override
    public UpdateMediaKitItemCommand toCommand() {
        return new UpdateMediaKitItemCommand(
                type,
                title,
                content);
    }
}
