package com.soodora.service.post;

import com.soodora.domain.post.CommentPostCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentPostRequest(@Schema(description = "Comment content", example = "My comment") String content)
        implements
            Request<CommentPostCommand> {

    @Override
    public CommentPostCommand toCommand() {
        if (content == null || content.isBlank())
            throw new InvalidRequestException("Comment content cannot be empty");

        return new CommentPostCommand(content.trim());
    }
}
