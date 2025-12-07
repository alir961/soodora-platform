package com.soodora.domain.mediakititem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MediaKitItem {

    private String mediaKitSlug;
    private String slug;
    private String type;
    private String title;
    private String content;

}
