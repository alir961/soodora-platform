package com.soodora.service.mediakit;

import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.service.common.Request;

public record UpdateMediaKitItemRequest(String type,
        String title,
        String content) implements Request<UpdateMediaKitItemCommand> {

    @Override
    public UpdateMediaKitItemCommand toCommand() {
        return new UpdateMediaKitItemCommand(
                type,
                title,
                content);
    }
}
