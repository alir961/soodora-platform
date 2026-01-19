package com.soodora.api.linkhub;

import com.soodora.domain.linkhub.LinkHub;

public record LinkHubAnalyticsDTO(
        int viewCount) {

    public static LinkHubAnalyticsDTO build(LinkHub analytics) {
        return new LinkHubAnalyticsDTO(analytics.getViewCount());
    }

}
