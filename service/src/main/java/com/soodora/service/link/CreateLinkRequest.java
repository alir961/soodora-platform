package com.soodora.service.link;

import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record CreateLinkRequest(String title,
        String description,
        String url,
        String iconUrl) implements Request<CreateLinkCommand> {

    @Override
    public CreateLinkCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (description == null || description.isEmpty())
            throw new InvalidRequestException("Description is not valid");
        if (url == null || url.isEmpty())
            throw new InvalidRequestException("Url is not valid");
        if (iconUrl == null || iconUrl.isEmpty())
            throw new InvalidRequestException("iconUrl is not valid");

        return new CreateLinkCommand(title.trim(), description.trim(), url, iconUrl);
    }
}
