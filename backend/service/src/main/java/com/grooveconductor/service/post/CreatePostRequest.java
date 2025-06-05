package com.grooveconductor.service.post;

import com.grooveconductor.domain.post.CreatePostCommand;
import com.grooveconductor.domain.post.PostType;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.common.Request;

import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.util.stream.Collectors.joining;

public record CreatePostRequest(
        String title,
        String content,
        String imageUrl,
        String audioUrl,
        @Schema(description = "Post Type in the following format: ANNOUNCE, MERCH_DROP, NEW_EVENT", example = "ANNOUNCE") String type)
        implements
            Request<CreatePostCommand> {

    static String typeValues = Stream.of(PostType.values())
            .map(Enum::toString)
            .collect(joining(", "));

    @Override
    public CreatePostCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (content == null || content.isEmpty())
            throw new InvalidRequestException("Content is not valid");
        if (type == null || !isValidPostType(type))
            throw new InvalidRequestException("Post type is not valid (values: " + typeValues + ")");
        PostType typeState = PostType.valueOf(type.toUpperCase());

        return new CreatePostCommand(title.trim(), content.trim(), imageUrl, audioUrl, typeState);
    }

    private boolean isValidPostType(String type) {
        for (PostType element : PostType.values()) {
            if (element.name().equalsIgnoreCase(type))
                return true;
        }
        return false;
    }

}
