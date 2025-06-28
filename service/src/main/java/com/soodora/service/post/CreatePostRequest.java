package com.soodora.service.post;

import com.soodora.domain.post.CreatePostCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record CreatePostRequest(
        String title,
        String content,
        String imageUrl,
        String audioUrl)
        implements
            Request<CreatePostCommand> {

    @Override
    public CreatePostCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (content == null || content.isEmpty())
            throw new InvalidRequestException("Content is not valid");

        return new CreatePostCommand(title.trim(), content.trim(), imageUrl, audioUrl);
    }

}
