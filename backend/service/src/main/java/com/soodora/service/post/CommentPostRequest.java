package com.soodora.service.post;

import com.soodora.domain.post.CommentPostCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record CommentPostRequest(String content) implements Request<CommentPostCommand> {

    @Override
    public CommentPostCommand toCommand() {
        if (content == null || content.isBlank())
            throw new InvalidRequestException("Comment content cannot be empty");

        return new CommentPostCommand(content.trim());
    }
}
