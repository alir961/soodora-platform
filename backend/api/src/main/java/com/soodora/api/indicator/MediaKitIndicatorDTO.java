package com.soodora.api.indicator;

import com.soodora.domain.indicator.MediaKitIndicator;

public record MediaKitIndicatorDTO(
        int soodoraKits,
        int soodoraKitItems,
        int downloadCount) {

    public static MediaKitIndicatorDTO build(MediaKitIndicator indicator) {
        return new MediaKitIndicatorDTO(
                indicator.getSoodoraKits(),
                indicator.getSoodoraKitItems(),
                indicator.getDownloadCount());
    }

}
