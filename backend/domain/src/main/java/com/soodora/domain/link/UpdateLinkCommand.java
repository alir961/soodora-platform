package com.soodora.domain.link;

import lombok.Getter;

@Getter
public class UpdateLinkCommand {
    private final String title;
    private final String description;
    private final String url;
    private final String iconUrl;

    public UpdateLinkCommand(
            String title,
            String description,
            String url,
            String iconUrl) {

        this.title = title;
        this.description = description;
        this.url = url;
        this.iconUrl = iconUrl;
    }
}
