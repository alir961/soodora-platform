package com.soodora.domain.mediakit;

public record CreateMediaKitCommand(String title, String shortBio, String longBio,
        String contactEmail, String contactPhone) {
}
