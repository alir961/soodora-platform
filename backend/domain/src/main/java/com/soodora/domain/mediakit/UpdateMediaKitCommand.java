package com.soodora.domain.mediakit;

public record UpdateMediaKitCommand(String title, String shortBio, String longBio,
        String contactEmail, String contactPhone) {
}
