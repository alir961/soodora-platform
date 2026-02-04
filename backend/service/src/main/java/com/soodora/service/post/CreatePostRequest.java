package com.soodora.service.post;

import com.soodora.domain.post.CreatePostCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePostRequest(
        @Schema(description = "Post title", example = "New Drop !") String title,
        @Schema(description = "Post content", example = "Hello ! Here is my new song stream it !") String content,
        @Schema(description = "Audio title", example = "Tribe Of Noise") String audioTitle,
        @Schema(description = "Audio url", example = "https://files.freemusicarchive.org/storage-freemusicarchive-org/tribe-of-noise-pro/music/8f71c11826e19070f080957ee1275f62.mp3") String audioUrl)
        implements
            Request<CreatePostCommand> {

    @Override
    public CreatePostCommand toCommand() {
        boolean isBasicPost = title != null && !title.isEmpty() && title.length() <= 255
                && content != null && !content.isEmpty() && content.length() <= 5000;

        boolean isAudioPost = audioTitle != null && !audioTitle.isEmpty() && audioTitle.length() <= 255
                && audioUrl != null && !audioUrl.isEmpty();

        if (!isBasicPost && !isAudioPost) {
            throw new InvalidRequestException(
                    "Invalid request: must be either a Basic Post (title + content) or an Audio Post (audioTitle + audioUrl)");
        }

        return new CreatePostCommand(
                title != null ? title.trim() : null,
                content != null ? content.trim() : null,
                audioTitle != null ? audioTitle.trim() : null,
                audioUrl);
    }

}
