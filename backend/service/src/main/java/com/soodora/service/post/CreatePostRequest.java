package com.soodora.service.post;

import com.soodora.domain.post.CreatePostCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePostRequest(
        @Schema(description = "Post title", example = "New Drop !") String title,
        @Schema(description = "Post content", example = "Hello ! Here is my new song stream it !") String content,
        @Schema(description = "Playlist name", example = "Tribe Of Noise") String audioTitle,
        @Schema(description = "Playlist name", example = "https://files.freemusicarchive.org/storage-freemusicarchive-org/tribe-of-noise-pro/music/8f71c11826e19070f080957ee1275f62.mp3") String audioUrl)
        implements
            Request<CreatePostCommand> {

    @Override
    public CreatePostCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (content == null || content.isEmpty())
            throw new InvalidRequestException("Content is not valid");
        if (audioUrl != null && !audioUrl.isEmpty())
            if (audioTitle == null || audioTitle.isEmpty())
                throw new InvalidRequestException("Audio Title is not valid");

        return new CreatePostCommand(title.trim(), content.trim(), audioTitle.trim(), audioUrl);
    }

}
