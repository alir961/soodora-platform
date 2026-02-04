package com.soodora.domain.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAnalytics {

    private int likes;
    private int comments;
    private int reposts;
    private int subscribedLikes;
    private int subscribedComments;
    private int subscribedReposts;

    public PostAnalytics append(PostAnalytics analytics) {
        return new PostAnalytics(
                likes + analytics.likes,
                comments + analytics.comments,
                reposts + analytics.reposts,
                subscribedLikes + analytics.subscribedLikes,
                subscribedComments + analytics.subscribedComments,
                subscribedReposts + analytics.subscribedReposts);
    }

    public static PostAnalytics empty() {
        return new PostAnalytics(0, 0, 0, 0, 0, 0);
    }

}
