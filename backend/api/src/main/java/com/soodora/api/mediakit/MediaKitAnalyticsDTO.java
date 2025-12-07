package com.soodora.api.mediakit;

import com.soodora.domain.mediakit.MediaKit;

public record MediaKitAnalyticsDTO(
        int downloadCount,
        int subscriberDownloadCount) {

    public static MediaKitAnalyticsDTO build(MediaKit mediaKit) {
        return new MediaKitAnalyticsDTO(
                mediaKit.getDownloadCount(),
                mediaKit.getSubscriberDownloadCount());
    }

}
