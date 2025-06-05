package com.grooveconductor.service.linkpage;

import com.grooveconductor.domain.linkpage.UpdateLinkPageCommand;
import com.grooveconductor.service.common.Request;

public record UpdateLinkPageRequest(String title,
        String description) implements Request<UpdateLinkPageCommand> {

    @Override
    public UpdateLinkPageCommand toCommand() {
        return new UpdateLinkPageCommand(
                title,
                description);
    }
}
