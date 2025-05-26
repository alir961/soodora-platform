package com.grooveconductor.service.link;

import com.grooveconductor.domain.link.UpdateLinkCommand;
import com.grooveconductor.service.common.Request;

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
