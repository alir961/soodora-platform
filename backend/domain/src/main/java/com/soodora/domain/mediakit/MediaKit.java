package com.soodora.domain.mediakit;

import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.user.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MediaKit {

    private String slug;
    private User owner;
    private String title;
    private String shortBio;
    private String longBio;
    private String contactEmail;
    private String contactPhone;
    private List<MediaKitItem> items;
    private int downloadCount;
    private int subscriberDownloadCount;

}
