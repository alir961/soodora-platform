package com.soodora.service.link;

import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.service.common.Request;

public record UpdateLinkRequest(String title,
        String description,
        String url,
        String iconUrl) implements Request<UpdateLinkCommand> {

    @Override
    public UpdateLinkCommand toCommand() {
        return new UpdateLinkCommand(
                title,
                description,
                url,
                iconUrl);
    }
}
