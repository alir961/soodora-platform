package com.grooveconductor.service.linkpage;

import com.grooveconductor.domain.linkpage.CreateLinkPageCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

public record CreateLinkPageRequest(String title,
        String description) implements Request<CreateLinkPageCommand> {

    @Override
    public CreateLinkPageCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (description == null || description.isEmpty())
            throw new InvalidRequestException("Description is not valid");

        return new CreateLinkPageCommand(title.trim(), description.trim());
    }
}
