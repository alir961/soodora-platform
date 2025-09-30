package com.soodora.domain.link;

public record UpdateLinkCommand(String title, String description, String url, String iconUrl) {
}
