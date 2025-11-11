package com.soodora.api.post;

import com.soodora.service.post.PostAnalytics;

public record PostAnalyticsDTO(
        int likes,
        int comments,
        int reposts,
        int subscribedLikes,
        int subscribedComments,
        int subscribedReposts) {

    public static PostAnalyticsDTO build(PostAnalytics analytics) {
        return new PostAnalyticsDTO(
                analytics.getLikes(), analytics.getComments(), analytics.getReposts(),
                analytics.getSubscribedLikes(), analytics.getSubscribedComments(), analytics.getSubscribedReposts());
    }

}
