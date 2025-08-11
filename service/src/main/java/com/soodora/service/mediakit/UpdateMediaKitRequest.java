package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.UpdateMediaKitCommand;
import com.soodora.service.common.Request;

public record UpdateMediaKitRequest(String title,
        String shortBio,
        String longBio,
        String contactEmail,
        String contactPhone) implements Request<UpdateMediaKitCommand> {

    @Override
    public UpdateMediaKitCommand toCommand() {
        return new UpdateMediaKitCommand(
                title,
                shortBio,
                longBio,
                contactEmail,
                contactPhone);
    }
}
