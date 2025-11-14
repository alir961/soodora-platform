package com.soodora.domain.link;

import com.soodora.domain.common.value.Url;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Link {

    private String slug;
    private String title;
    private String description;
    private Url url;
    private Url iconUrl;

}
