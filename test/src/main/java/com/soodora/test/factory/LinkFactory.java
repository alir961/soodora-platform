package com.soodora.test.factory;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.link.Link;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class LinkFactory {

    public static Link buildDefaultLink(String slug, String name) {
        return new Link(
                slug,
                name,
                "Description",
                new Url("https://soodora.com/"),
                new Url("https://soodora.com/icon.jpg"));
    }

}
