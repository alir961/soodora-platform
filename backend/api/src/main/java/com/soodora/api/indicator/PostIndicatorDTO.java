package com.soodora.api.indicator;

import com.soodora.domain.indicator.PostIndicator;

public record PostIndicatorDTO(
        int numberOfPosts,
        int numberOfAudios,
        int numberOfLikes,
        int numberOfComments,
        int numberOfReposts,
        int numberOfPostsWithoutEngagement) {

    public static PostIndicatorDTO build(PostIndicator indicator) {
        return new PostIndicatorDTO(
                indicator.getNumberOfPosts(),
                indicator.getNumberOfAudios(),
                indicator.getNumberOfLikes(),
                indicator.getNumberOfComments(),
                indicator.getNumberOfReposts(),
                indicator.getNumberOfPostsWithoutEngagement());
    }

}
