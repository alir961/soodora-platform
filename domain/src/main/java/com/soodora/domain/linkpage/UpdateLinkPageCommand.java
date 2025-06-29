package com.soodora.domain.linkpage;

import lombok.Getter;

@Getter
public class UpdateLinkPageCommand {
    private final String title;
    private final String description;

    public UpdateLinkPageCommand(
            String title,
            String description) {

        this.title = title;
        this.description = description;
    }
}
