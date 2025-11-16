package com.soodora.domain.mediakititem;

import com.soodora.domain.mediakit.MediaKit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MediaKitItem {

    private MediaKit mediaKit;
    private String slug;
    private String type;
    private String title;
    private String content;

}
