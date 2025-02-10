package com.grooveconductor.service.post;

import com.grooveconductor.domain.post.CreatePostCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

public record CreatePostRequest(
        String title,
        String content) implements Request<CreatePostCommand> {

    @Override
    public CreatePostCommand toCommand() {
        if (title == null)
            throw new InvalidRequestException("Title is not valid");
        if (content == null)
            throw new InvalidRequestException("Content is not valid");

        return new CreatePostCommand(title, content);
    }

}
