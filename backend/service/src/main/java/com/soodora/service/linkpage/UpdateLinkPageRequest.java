package com.soodora.service.linkpage;

import com.soodora.domain.linkpage.UpdateLinkPageCommand;
import com.soodora.service.common.Request;

public record UpdateLinkPageRequest(String title,
        String description) implements Request<UpdateLinkPageCommand> {

    @Override
    public UpdateLinkPageCommand toCommand() {
        return new UpdateLinkPageCommand(
                title,
                description);
    }
}
