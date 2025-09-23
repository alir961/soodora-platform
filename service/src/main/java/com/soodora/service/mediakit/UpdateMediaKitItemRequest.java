package com.soodora.service.mediakit;

import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateMediaKitItemRequest(
        @Schema(description = "Type of the item", example = "Image", defaultValue = "Image") String type,
        @Schema(description = "Title of the item", example = "My Item", defaultValue = "My Item") String title,
        @Schema(description = "Content of the item", example = "imageUrl", defaultValue = "imageUrl") String content)
        implements
            Request<UpdateMediaKitItemCommand> {

    @Override
    public UpdateMediaKitItemCommand toCommand() {
        if (!isStringValid(type, 50))
            throw new InvalidRequestException("Type cannot exceed 50 characters");
        if (!isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot exceed 255 characters");
        if (!isStringValid(content, 5000))
            throw new InvalidRequestException("Content cannot exceed 5000 characters");

        return new UpdateMediaKitItemCommand(type.trim(), title.trim(), content.trim());
    }
}
