package com.soodora.api.indicator;

import com.soodora.domain.indicator.LinkHubIndicator;

public record LinkHubIndicatorDTO(
        int linkHubs,
        int links,
        int viewCount) {

    public static LinkHubIndicatorDTO build(LinkHubIndicator indicator) {
        return new LinkHubIndicatorDTO(
                indicator.getLinkHubs(),
                indicator.getLinks(),
                indicator.getViewCount());
    }

}
