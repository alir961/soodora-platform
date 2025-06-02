package com.grooveconductor.service.post;

import com.grooveconductor.domain.post.CommentPostCommand;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

public record CommentPostRequest(String content) implements Request<CommentPostCommand> {

    @Override
    public CommentPostCommand toCommand() {
        if (content == null || content.isBlank())
            throw new InvalidRequestException("Comment content cannot be empty");

        return new CommentPostCommand(content.trim());
    }
}
